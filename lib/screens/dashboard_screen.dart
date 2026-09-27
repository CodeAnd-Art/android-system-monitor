import 'package:flutter/material.dart';

import '../models/system_stats.dart';
import '../services/system_monitor_service.dart';

class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  final SystemMonitorService _service = SystemMonitorService();
  late Future<SystemStats> _statsFuture;

  @override
  void initState() {
    super.initState();
    _statsFuture = _service.fetchSystemStats();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('SysMonitor Pro'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              setState(() {
                _statsFuture = _service.fetchSystemStats();
              });
            },
          )
        ],
      ),
      body: FutureBuilder<SystemStats>(
        future: _statsFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          final stats = snapshot.data ?? SystemStats.empty();

          return Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Platform: ${stats.platform}',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 20),
                Wrap(
                  spacing: 16,
                  runSpacing: 16,
                  children: [
                    MetricCard(
                      title: 'CPU',
                      value: '${stats.cpuUsage.toStringAsFixed(1)}%',
                      subtitle: stats.status,
                      color: Colors.orange,
                    ),
                    MetricCard(
                      title: 'Memory',
                      value: stats.memoryText,
                      subtitle: '${stats.memoryUsagePercent.toStringAsFixed(1)}% used',
                      color: Colors.cyan,
                    ),
                    MetricCard(
                      title: 'Disk',
                      value: stats.diskText,
                      subtitle: 'Storage usage',
                      color: Colors.green,
                    ),
                  ],
                )
              ],
            ),
          );
        },
      ),
    );
  }
}

class MetricCard extends StatelessWidget {
  const MetricCard({
    super.key,
    required this.title,
    required this.value,
    required this.subtitle,
    required this.color,
  });

  final String title;
  final String value;
  final String subtitle;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 220,
      child: Card(
        elevation: 3,
        child: Padding(
          padding: const EdgeInsets.all(18),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: Theme.of(context).textTheme.labelLarge,
              ),
              const SizedBox(height: 12),
              Text(
                value,
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: color,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                subtitle,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
