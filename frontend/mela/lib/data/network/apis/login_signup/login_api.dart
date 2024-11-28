import 'package:dio/dio.dart';
import 'package:mela/data/network/constants/endpoints_const.dart';
import 'package:mela/data/network/dio_client.dart';
import 'package:mela/domain/entity/user/token_model.dart';
import 'package:mela/domain/usecase/user_login/login_usecase.dart';

class LoginApi {
  final DioClient _dioClient;
  LoginApi(this._dioClient);
  Future<TokenModel> login(LoginParams loginParams) async {
    try {
      final responseData = await _dioClient.post(
        EndpointsConst.login,
        options: Options(headers: {'Content-Type': 'application/json'}),
        data: loginParams.toJson(),
      );
      

      //-test
      final requestOptions = RequestOptions(path: '/api/login');
      if (DateTime.now().minute % 2 == 0) {
        throw DioException(
          requestOptions: requestOptions,
          response: Response(
            requestOptions: requestOptions,
            statusCode: 401,
            data: {
              "message": "Invalid username or password",
              "status": "UNAUTHORIZED",
              "time": "2024-11-26T19:58:40.3939856",
            },
          ),
        );
      }

      return TokenModel.fromJson(responseData);
    } catch (e) {
      rethrow;
    }
  }
}
