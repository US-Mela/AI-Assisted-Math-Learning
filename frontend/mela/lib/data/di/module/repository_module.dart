import 'dart:async';

import 'package:mela/domain/repository/post/post_repository.dart';
import 'package:mela/domain/repository/setting/setting_repository.dart';
import 'package:mela/domain/repository/topic/topic_repository.dart';
import 'package:mela/domain/repository/user/user_repository.dart';

import '../../../di/service_locator.dart';
import '../../local/datasources/post/post_datasource.dart';
import '../../network/apis/posts/post_api.dart';
import '../../repository/post/post_repository_impl.dart';
import '../../repository/setting/setting_repository_impl.dart';
import '../../repository/topic/topic_repository_impl.dart';
import '../../repository/user/user_repository_impl.dart';
import '../../sharedpref/shared_preference_helper.dart';

class RepositoryModule {
  static Future<void> configureRepositoryModuleInjection() async {
    // repository:--------------------------------------------------------------
    getIt.registerSingleton<SettingRepository>(SettingRepositoryImpl(
      getIt<SharedPreferenceHelper>(),
    ));

    getIt.registerSingleton<UserRepository>(UserRepositoryImpl(
      getIt<SharedPreferenceHelper>(),
    ));

    getIt.registerSingleton<PostRepository>(PostRepositoryImpl(
      getIt<PostApi>(),
      getIt<PostDataSource>(),
    ));
    getIt.registerSingleton<TopicRepository>(TopicRepositoryImpl(
    ));
  }
}
