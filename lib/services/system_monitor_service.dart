import 'dart:convert';
import 'dart:io';

import '../models/system_stats.dart';

class SystemMonitorService {
  Future<SystemStats> fetchSystemStats() async {
    try {
      final platform = Platform.operatingSystem;

      if (platform == 'linux') {
        return _readLinuxStats();
      }

      if (platform == 'macos') {
        return _readMacStats();
      }

      if (platform == 'windows') {
        return _readWindowsStats();
      }

      if (platform == 'android') {
        return _readAndroidStats();
      }

      return SystemStats(
        cpuUsage: 0,
        memoryUsagePercent: 0,
        memoryUsedMb: 0,
        memoryTotalMb: 0,
        diskUsedMb: 0,
        diskTotalMb: 0,
        platform: platform,
        status: 'Platform not supported for direct system monitoring',
      );
    } catch (_) {
      return SystemStats.empty();
    }
  }

  Future<SystemStats> _readLinuxStats() async {
    final meminfo = await File('/proc/meminfo').readAsString();
    final stat = await File('/proc/stat').readAsString();
    final disk = await _getDiskStats();

    final memTotal = _parseMeminfoValue(meminfo, 'MemTotal');
    final memAvailable = _parseMeminfoValue(meminfo, 'MemAvailable');
    final memUsed = (memTotal - memAvailable) ~/ 1024;
    final cpuUsage = _parseCpuUsage(stat);

    return SystemStats(
      cpuUsage: cpuUsage,
      memoryUsagePercent: memTotal > 0 ? ((memUsed / (memTotal / 1024)) * 100).clamp(0.0, 100.0) : 0,
      memoryUsedMb: memUsed,
      memoryTotalMb: memTotal ~/ 1024,
      diskUsedMb: disk.usedMb,
      diskTotalMb: disk.totalMb,
      platform: 'Linux',
      status: 'Available via /proc',
    );
  }

  Future<SystemStats> _readMacStats() async {
    final mem = await Process.run('sysctl', ['-n', 'hw.memsize']);
    final used = await Process.run('vm_stat', []);
    final totalMb = (int.tryParse((mem.stdout as String).trim()) ?? 0) ~/ (1024 * 1024);

    final text = used.stdout.toString();
    final pagesFree = _extractValueFromVmStat(text, 'Pages free');
    final pagesActive = _extractValueFromVmStat(text, 'Pages active');
    final pagesInactive = _extractValueFromVmStat(text, 'Pages inactive');
    final pagesWired = _extractValueFromVmStat(text, 'Pages wired down');
    final usedPages = pagesActive + pagesInactive + pagesWired;
    final usedMb = (usedPages * 4096 / (1024 * 1024)).round();
    final percent = totalMb > 0 ? ((usedMb / totalMb) * 100).clamp(0.0, 100.0) : 0.0;

    return SystemStats(
      cpuUsage: 0,
      memoryUsagePercent: percent,
      memoryUsedMb: usedMb,
      memoryTotalMb: totalMb,
      diskUsedMb: 0,
      diskTotalMb: 0,
      platform: 'macOS',
      status: 'macOS info available',
    );
  }

  Future<SystemStats> _readWindowsStats() async {
    final cpu = await Process.run('wmic', ['cpu', 'get', 'LoadPercentage']);
    final mem = await Process.run('wmic', ['os', 'get', 'TotalVisibleMemorySize,FreePhysicalMemory']);

    final cpuValue = _extractLoadPercentage(cpu.stdout.toString());
    final memText = mem.stdout.toString();
    final total = _extractWindowsMemoryValue(memText, 'TotalVisibleMemorySize');
    final free = _extractWindowsMemoryValue(memText, 'FreePhysicalMemory');
    final used = ((total - free) / 1024).round();
    final totalMb = (total / 1024).round();

    return SystemStats(
      cpuUsage: cpuValue.toDouble(),
      memoryUsagePercent: totalMb > 0 ? ((used / totalMb) * 100).clamp(0.0, 100.0) : 0,
      memoryUsedMb: used,
      memoryTotalMb: totalMb,
      diskUsedMb: 0,
      diskTotalMb: 0,
      platform: 'Windows',
      status: 'Windows counters available',
    );
  }

  Future<SystemStats> _readAndroidStats() async {
    final memInfo = await File('/proc/meminfo').readAsString();
    final stat = await File('/proc/stat').readAsString();

    final total = _parseMeminfoValue(memInfo, 'MemTotal');
    final available = _parseMeminfoValue(memInfo, 'MemAvailable');
    final usedMb = ((total - available) / 1024).round();
    final totalMb = (total / 1024).round();

    return SystemStats(
      cpuUsage: _parseCpuUsage(stat),
      memoryUsagePercent: total > 0 ? (((total - available) / total) * 100).clamp(0.0, 100.0) : 0,
      memoryUsedMb: usedMb,
      memoryTotalMb: totalMb,
      diskUsedMb: 0,
      diskTotalMb: 0,
      platform: 'Android',
      status: 'Android /proc stats available',
    );
  }

  static int _parseMeminfoValue(String content, String key) {
    final match = RegExp('$key\\s*:\\s*(\\d+)\\s*kB').firstMatch(content);
    return match != null ? int.parse(match.group(1)!) * 1024 : 0;
  }

  static double _parseCpuUsage(String statContent) {
    final lines = statContent.split('\n');
    final cpuLine = lines.firstWhere(
      (line) => line.startsWith('cpu '),
      orElse: () => '',
    );

    if (cpuLine.isEmpty) return 0;

    final values = cpuLine.split(RegExp(r'\s+')).skip(1).map(int.parse).toList();
    if (values.length < 4) return 0;

    final user = values[0];
    final nice = values[1];
    final system = values[2];
    final idle = values[3];
    final total = user + nice + system + idle;

    if (total == 0) return 0;
    final busy = user + nice + system;
    return ((busy / total) * 100).clamp(0.0, 100.0);
  }

  static int _extractValueFromVmStat(String text, String key) {
    final match = RegExp('$key:\\s*(\\d+)').firstMatch(text);
    return match != null ? int.parse(match.group(1)!) : 0;
  }

  static int _extractLoadPercentage(String output) {
    final values = output.split(RegExp(r'\s+')).where((v) => v.isNotEmpty).toList();
    for (final value in values) {
      final parsed = int.tryParse(value);
      if (parsed != null) return parsed;
    }
    return 0;
  }

  static int _extractWindowsMemoryValue(String text, String key) {
    final lines = text.split('\n');
    for (final line in lines) {
      if (line.toLowerCase().contains(key.toLowerCase())) {
        final parts = line.split(RegExp(r'\s+')).where((p) => p.isNotEmpty).toList();
        if (parts.length >= 2) {
          return int.tryParse(parts[1]) ?? 0;
        }
      }
    }
    return 0;
  }

  Future<_DiskSummary> _getDiskStats() async {
    if (Platform.isLinux || Platform.isAndroid) {
      final df = await Process.run('df', ['-k', '/']);
      final lines = df.stdout.toString().split('\n');
      if (lines.length >= 2) {
        final parts = lines[1].split(RegExp(r'\s+'));
        if (parts.length >= 5) {
          final total = int.tryParse(parts[1]) ?? 0;
          final used = int.tryParse(parts[2]) ?? 0;
          return _DiskSummary(total ~/ 1024, used ~/ 1024);
        }
      }
    }

    return const _DiskSummary(0, 0);
  }
}

class _DiskSummary {
  final int totalMb;
  final int usedMb;

  const _DiskSummary(this.totalMb, this.usedMb);
}
