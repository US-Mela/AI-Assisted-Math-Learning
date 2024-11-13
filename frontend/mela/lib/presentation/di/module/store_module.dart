import 'dart:async';

import 'package:mela/core/stores/error/error_store.dart';
import 'package:mela/core/stores/form/form_store.dart';
import 'package:mela/domain/repository/setting/setting_repository.dart';
import 'package:mela/domain/usecase/post/get_post_usecase.dart';
import 'package:mela/domain/usecase/question/get_questions_usecase.dart';
import 'package:mela/domain/usecase/user/is_logged_in_usecase.dart';
import 'package:mela/domain/usecase/user/login_usecase.dart';
import 'package:mela/domain/usecase/user/save_login_in_status_usecase.dart';
import 'package:mela/presentation/home/store/language/language_store.dart';
import 'package:mela/presentation/home/store/theme/theme_store.dart';
import 'package:mela/presentation/login/store/login_store.dart';
import 'package:mela/presentation/post/store/post_store.dart';
import 'package:mela/presentation/question/store/single_question/single_question_store.dart';
import 'package:mela/presentation/question/store/timer/timer_store.dart';

import '../../../di/service_locator.dart';
import '../../question/store/question_store.dart';

class StoreModule {
  static Future<void> configureStoreModuleInjection() async {
    // factories:---------------------------------------------------------------
    getIt.registerFactory(() => ErrorStore());
    getIt.registerFactory(() => FormErrorStore());
    getIt.registerFactory(
      () => FormStore(getIt<FormErrorStore>(), getIt<ErrorStore>()),
    );

    // stores:------------------------------------------------------------------
    getIt.registerSingleton<UserStore>(
      UserStore(
        getIt<IsLoggedInUseCase>(),
        getIt<SaveLoginStatusUseCase>(),
        getIt<LoginUseCase>(),
        getIt<FormErrorStore>(),
        getIt<ErrorStore>(),
      ),
    );

    getIt.registerSingleton<PostStore>(
      PostStore(
        getIt<GetPostUseCase>(),
        getIt<ErrorStore>(),
      ),
    );

    getIt.registerSingleton<ThemeStore>(
      ThemeStore(
        getIt<SettingRepository>(),
        getIt<ErrorStore>(),
      ),
    );

    getIt.registerSingleton<LanguageStore>(
      LanguageStore(
        getIt<SettingRepository>(),
        getIt<ErrorStore>(),
      ),
    );
    
    getIt.registerSingleton<SingleQuestionStore>(
      SingleQuestionStore(
      )
    );

    getIt.registerSingleton<QuestionStore>(
      QuestionStore(
        getIt<GetQuestionsUseCase>(),
        getIt<ErrorStore>(),
      )
    );

    getIt.registerSingleton<TimerStore>(
     TimerStore()
    );
  }
}