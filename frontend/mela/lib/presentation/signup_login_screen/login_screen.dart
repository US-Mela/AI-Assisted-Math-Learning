import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/di/service_locator.dart';
import 'package:mela/utils/check_inputs/check_input.dart';
import 'package:mobx/mobx.dart';

import '../../constants/assets.dart';
import '../../core/widgets/progress_indicator_widget.dart';
import '../../utils/routes/routes.dart';
import 'store/login_or_signup_store/login_or_signup_store.dart';
import 'store/user_login_store/user_login_store.dart';
import 'widgets/login_or_sign_up_button.dart';
import 'widgets/third_party_button.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            physics: const BouncingScrollPhysics(),
            child: _FormContent(),
          ),
        ),
      ),
    );
  }
}

class _FormContent extends StatefulWidget {
  const _FormContent({Key? key}) : super(key: key);

  @override
  State<_FormContent> createState() => __FormContentState();
}

class __FormContentState extends State<_FormContent> {
  //stores:---------------------------------------------------------------------
  final UserLoginStore _userLoginStore = getIt<UserLoginStore>();
  final LoginOrSignupStore _loginOrSignupStore = getIt<LoginOrSignupStore>();

  //controllers:-----------------------------------------------------------------
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  //disposers:-----------------------------------------------------------------
  late final ReactionDisposer _loginReactionDisposer;
  late final ReactionDisposer _errorLoginReactionDisposer;
  @override
  void initState() {
    super.initState();

    _loginReactionDisposer =
        reaction((_) => _userLoginStore.isLoggedIn, (bool success) {
      print(
          "---------------------------------------->LoginScreen1 ${_userLoginStore.isLoggedIn ? "true" : "false"}");
      if (success) {
        print(
            "---------------------------------------->LoginScreen2 ${_userLoginStore.isLoggedIn ? "true" : "false"}");
        Navigator.of(context).pushNamedAndRemoveUntil(
            Routes.allScreens, (Route<dynamic> route) => false);
        _userLoginStore.resetSettingForLogin();
      }
    });
    _errorLoginReactionDisposer = reaction(
        (_) => _userLoginStore.errorStore.errorMessage, (String errorMessage) {
      if (errorMessage.isNotEmpty) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text(errorMessage)));
      }
    });
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    //for logout from another screen when refreshToken is expired
    print("}}}}}}}}}}}}}}}-------}}}}}}}}}}}}}}}");
    if (!_userLoginStore.isSetLoginLoading) {
      print("}}}}}}}}}}}}}}}}*****}}}}}}}}}}}}}}");
      _userLoginStore.setIsLogin();
    }
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _loginReactionDisposer();
    _errorLoginReactionDisposer();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      child: Stack(
        alignment: Alignment.center,
        children: [
          Observer(builder: (context) {
            return buildContentInLoginScreen();
          }),
          Observer(
            builder: (context) {
              return Visibility(
                visible: _userLoginStore.isLoading,
                child: AbsorbPointer(
                  absorbing: true,
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      Container(
                        color: Theme.of(context)
                            .colorScheme
                            .primary
                            .withOpacity(0.8),
                      ),
                      const CustomProgressIndicatorWidget(),
                    ],
                  ),
                ),
              );
            },
          )
        ],
      ),
    );
  }

  Widget buildContentInLoginScreen() {
    if (_userLoginStore.isSetLoginLoading) {
      return const Center(
        child: CustomProgressIndicatorWidget(),
      );
    }
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 30),
      constraints: const BoxConstraints(maxWidth: 380),
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            //Title and Subtitle
            Text("Đăng nhập",
                style: Theme.of(context)
                    .textTheme
                    .bigTitle
                    .copyWith(color: Theme.of(context).colorScheme.primary)),
            const SizedBox(height: 5),
            Text("Đăng nhập để tiếp tục hành trình của bạn",
                style: Theme.of(context)
                    .textTheme
                    .subTitle
                    .copyWith(color: Theme.of(context).colorScheme.secondary)),
            const SizedBox(height: 35),

            //Email TextField
            TextFormField(
              controller: _emailController,
              validator: (value) {
                return CheckInput.validateEmail(value);
              },
              decoration: InputDecoration(
                hintText: 'Nhập địa chỉ email',
                prefixIcon: const Icon(Icons.email_outlined, size: 25),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide.none,
                ),
                fillColor: Theme.of(context).colorScheme.onTertiary,
                filled: true,
                errorStyle: const TextStyle(
                  color: Colors.red,
                  fontSize: 14,
                ),
              ),
            ),
            const SizedBox(height: 16),

            //Password TextField
            Observer(
              builder: (context) {
                return TextFormField(
                  controller: _passwordController,
                  validator: (value) {
                    return CheckInput.validatePassword(value);
                  },
                  obscureText: !_userLoginStore.isPasswordVisible,
                  decoration: InputDecoration(
                    hintText: 'Nhập mật khẩu của bạn',
                    prefixIcon: Icon(Icons.lock_outline_rounded,
                        size: 25,
                        color: Theme.of(context).colorScheme.secondary),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: BorderSide.none,
                    ),
                    fillColor: Theme.of(context).colorScheme.onTertiary,
                    filled: true,
                    suffixIcon: IconButton(
                      icon: Icon(_userLoginStore.isPasswordVisible
                          ? Icons.visibility_off
                          : Icons.visibility),
                      onPressed: () =>
                          _userLoginStore.togglePasswordVisibility(),
                    ),
                    errorStyle: const TextStyle(
                      color: Colors.red,
                      fontSize: 14,
                    ),
                  ),
                );
              },
            ),
            const SizedBox(height: 16),

            //Forgot password
            GestureDetector(
              onTap: () {
                Navigator.of(context)
                    .pushNamed(Routes.enterEmailInForgotPasswordScreen);
              },
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text("Quên mật khẩu?",
                      style: Theme.of(context).textTheme.normal.copyWith(
                          color: Theme.of(context).colorScheme.secondary)),
                ],
              ),
            ),
            const SizedBox(height: 30),

            // //Button Login
            ButtonLoginOrSignUp(
                textButton: "Đăng nhập",
                onPressed: () async {
                  if (_formKey.currentState?.validate() ?? false) {
                    try {
                      await _userLoginStore.login(
                        _emailController.text,
                        _passwordController.text,
                      );
                      _emailController.clear();
                      _passwordController.clear();
                    } catch (e) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                            content: Text('Login failed: ${e.toString()}')),
                      );
                    }
                  }
                }),

            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('Hoặc tiếp tục với',
                    style: Theme.of(context).textTheme.normal.copyWith(
                        color: Theme.of(context).colorScheme.secondary)),
              ],
            ),
            const SizedBox(height: 16),
            //Sign Up by Third Party
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ThirdPartyButton(pathLogo: Assets.googleIcon, onPressed: () {}),
                const SizedBox(width: 20),
                ThirdPartyButton(
                    pathLogo: Assets.facebookIcon, onPressed: () {}),
              ],
            ),
            const SizedBox(height: 30),

            //Return sign up
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text("Chưa có tài khoản?",
                    style: Theme.of(context).textTheme.subTitle.copyWith(
                        color: Theme.of(context).colorScheme.secondary)),
                const SizedBox(
                  width: 4,
                ),
                GestureDetector(
                  onTap: () {
                    _loginOrSignupStore.toggleChangeScreen();
                    _userLoginStore.resetSettingForLogin();
                  },
                  child: Text(
                    "ĐĂNG KÝ",
                    style: Theme.of(context).textTheme.subTitle.copyWith(
                        color: Theme.of(context).colorScheme.tertiary,
                        decoration: TextDecoration.underline),
                  ),
                )
              ],
            )
          ],
        ),
      ),
    );
  }
}