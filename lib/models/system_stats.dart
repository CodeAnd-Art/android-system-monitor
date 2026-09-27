class SystemStats {
  final double cpuUsage;
  final double memoryUsagePercent;
  final int memoryUsedMb;
  final int memoryTotalMb;
  final int diskUsedMb;
  final int diskTotalMb;
  final String platform;
  final String status;

  const SystemStats({
    required this.cpuUsage,
    required this.memoryUsagePercent,
    required this.memoryUsedMb,
    required this.memoryTotalMb,
    required this.diskUsedMb,
    required this.diskTotalMb,
    required this.platform,
    required this.status,
  });

  factory SystemStats.empty() => const SystemStats(
        cpuUsage: 0,
        memoryUsagePercent: 0,
        memoryUsedMb: 0,
        memoryTotalMb: 0,
        diskUsedMb: 0,
        diskTotalMb: 0,
        platform: 'Unknown',
        status: 'Loading',
      );

  String get memoryText => '$memoryUsedMb MB / $memoryTotalMb MB';
  String get diskText => '$diskUsedMb MB / $diskTotalMb MB';
}
