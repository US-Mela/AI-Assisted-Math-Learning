import 'dart:async';
import 'package:flutter/material.dart';
import 'package:mela/di/service_locator.dart';
import 'package:mela/presentation/my_app.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // await setPreferredOrientations();
  await ServiceLocator.configureDependencies();
  runApp(MyApp());
}

// Future<void> setPreferredOrientations() {
//   return SystemChrome.setPreferredOrientations([
//     DeviceOrientation.portraitUp,
//     DeviceOrientation.portraitDown,
//     DeviceOrientation.landscapeRight,
//     DeviceOrientation.landscapeLeft,
//   ]);
// }