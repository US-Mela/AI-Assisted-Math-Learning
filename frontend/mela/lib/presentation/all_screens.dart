import 'package:flutter/material.dart';
import 'package:mela/presentation/stats/stats.dart';
import 'package:mela/presentation/personal/personal.dart';

import '../core/widgets/custom_navigation_bar.dart';
import 'chat/chat_screen.dart';
import 'courses_screen/courses_screen.dart';

class AllScreens extends StatefulWidget {
  @override
  _AllScreensState createState() => _AllScreensState();
}

class _AllScreensState extends State<AllScreens> {
  // Index for the currently selected tab
  int _currentIndex = 0;

  // List of screens for each tab
  final List<Widget> _screens = [
    CoursesScreen(),
    StatisticsScreen(),
    ChatScreen(),
    PersonalScreen(),
  ];

  void onTabTapped(int index) {
    setState(() {
      _currentIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_currentIndex],
      bottomNavigationBar: CustomNavigationBar(
        currentIndex: _currentIndex,
        screens: _screens,
        onTap: onTabTapped
      )
    );
  }
}