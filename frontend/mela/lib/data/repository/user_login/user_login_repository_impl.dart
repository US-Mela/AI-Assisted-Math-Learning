import 'dart:async';

import 'package:mela/data/network/apis/login_signup/login_api.dart';
import 'package:mela/data/network/apis/login_signup/refresh_access_token_api.dart';
import 'package:mela/data/securestorage/secure_storage_helper.dart';
import 'package:mela/domain/entity/user/token_model.dart';
import 'package:mela/domain/usecase/user_login/login_usecase.dart';

import '../../../domain/repository/user_login/user_login_repository.dart';
import '../../sharedpref/shared_preference_helper.dart';

class UserLoginRepositoryImpl extends UserLoginRepository {
  // shared pref object
  final SharedPreferenceHelper _sharedPrefsHelper;
  final SecureStorageHelper _secureStorageHelper;
  final LoginApi _loginApi;
  final RefreshAccessTokenApi _refreshAccessTokenApi;

  // constructor
  UserLoginRepositoryImpl(
      this._sharedPrefsHelper, this._secureStorageHelper, this._loginApi, this._refreshAccessTokenApi);

  // Login:---------------------------------------------------------------------
  @override
  Future<TokenModel?> login(LoginParams params)  {
    return _loginApi.login(params);
  }

  @override
  Future<void> saveIsLoggedIn(bool value) =>
      _sharedPrefsHelper.saveIsLoggedIn(value);

  @override
  Future<bool> get isLoggedIn => _sharedPrefsHelper.isLoggedIn;

  @override
  Future<void> saveAccessToken(String accessToken) {
    return _secureStorageHelper.saveAccessToken(accessToken);
  }

  @override
  Future<void> saveRefreshToken(String refreshToken) {
    return _secureStorageHelper.saveRefreshToken(refreshToken);
  }
  
  @override
  Future<String> refreshAccessToken() async{
    // TODO: implement refreshAccessToken
    return _refreshAccessTokenApi.refreshAccessToken(await _secureStorageHelper.refreshToken);
  }
}
