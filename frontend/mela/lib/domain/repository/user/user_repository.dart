import 'dart:async';

import 'package:mela/domain/usecase/user/login_usecase.dart';

import '../../entity/user/user.dart';

abstract class UserRepository {
  Future<User?> login(LoginParams params);

  Future<void> saveIsLoggedIn(bool value);

  Future<User> getUserInfo();
  Future<User> updateUserInfo(User newUser);

  Future<bool> get isLoggedIn;
}