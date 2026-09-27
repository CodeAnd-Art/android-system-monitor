import 'package:flutter/material.dart';

import 'theme/app_theme.dart';
import 'screens/dashboard_screen.dart';

class SysMonitorApp extends StatelessWidget {
  const SysMonitorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'SysMonitor Pro',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      home: const DashboardScreen(),
    );
  }
}
