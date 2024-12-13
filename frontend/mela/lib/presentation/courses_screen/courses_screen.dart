// import 'package:flutter/material.dart';
// import 'package:flutter_mobx/flutter_mobx.dart';
// import 'package:mela/constants/app_theme.dart';
// import 'package:mela/core/widgets/progress_indicator_widget.dart';
// import 'package:mela/presentation/courses_screen/store/topic_store/topic_store.dart';
// import 'package:mobx/mobx.dart';

// import '../../di/service_locator.dart';
// import '../../themes/default/colors_standards.dart';
// import '../../utils/routes/routes.dart';
// import '../lectures_in_topic_screen/widgets/lecture_item.dart';
// import 'widgets/cover_image_widget.dart';
// import 'widgets/topic_item.dart';

// class CoursesScreen extends StatefulWidget {
//   const CoursesScreen({super.key});

//   @override
//   State<CoursesScreen> createState() => _CoursesScreenState();
// }

// class _CoursesScreenState extends State<CoursesScreen> {
//   //stores:---------------------------------------------------------------------
//   final TopicStore _topicStore = getIt<TopicStore>();
//   late final ReactionDisposer _unAuthorizedReactionDisposer;
//   @override
//   void initState() {
//     super.initState();
//     //routeObserver.subscribe(this, ModalRoute.of(context));

//     //for only refresh token expired
//     _unAuthorizedReactionDisposer = reaction(
//       (_) => _topicStore.isUnAuthorized,
//       (value) {
//         if (value) {
//           _topicStore.isUnAuthorized = false;
//           _topicStore.resetErrorString();
//           //Remove all routes in stack
//           Navigator.of(context).pushNamedAndRemoveUntil(
//               Routes.loginOrSignupScreen, (route) => false);
//         }
//       },
//     );
//   }

//   @override
//   void didChangeDependencies() {
//     super.didChangeDependencies();
//     print("didChangeDependencies In CoursesScreen");
//     //print("========TopicError: ${_topicStore.errorString}");
//     // check to see if already called api
//     if (!_topicStore.loading) {
//       //_topicStore.getLevels();
//       _topicStore.getTopics();
//       //_topicStore.getAreLearningLectures();
//     }
//   }

//   @override
//   void dispose() {
//     //routeObserver.unsubscribe(this);
//     _unAuthorizedReactionDisposer();
//     super.dispose();
//   }

//   @override
//   Widget build(BuildContext context) {
//     //print("^^^^^^^^^^^^^^^^^^ErrorString in Courses_Screen1: ${_topicStore.errorString}");
//     return Scaffold(
//       appBar: AppBar(
//         title: Padding(
//           padding: const EdgeInsets.only(left: 10),
//           child: Text("MELA",
//               style: Theme.of(context)
//                   .textTheme
//                   .heading
//                   .copyWith(color: Theme.of(context).colorScheme.onPrimary)),
//         ),
//         actions: [
//           Observer(builder: (context) {
//             if (_topicStore.errorString.isNotEmpty ||
//                 _topicStore.topicList == null ||
//                 _topicStore.lecturesAreLearningList == null ||
//                 _topicStore.levelList == null) {
//               return const SizedBox.shrink();
//             }
//             return IconButton(
//               onPressed: () {
//                 //eg: incourse no wifi, enter search, turnon wifi, back to app
//                 _topicStore.resetErrorString();
//                 Navigator.of(context).pushNamed(Routes.searchScreen);
//               },
//               icon: const Icon(Icons.search),
//               color: Theme.of(context).colorScheme.onPrimary,
//             );
//           })
//         ],
//       ),
//       body: Observer(
//         builder: (context) {
//           //print("^^^^^^^^^^^^^^^^^^ErrorString in Courses_Screen2: ${_topicStore.errorString}");
//           if (_topicStore.loading) {
//             return AbsorbPointer(
//               absorbing: true,
//               child: Stack(
//                 alignment: Alignment.center,
//                 children: [
//                   Container(
//                     color:
//                         Theme.of(context).colorScheme.surface.withOpacity(0.8),
//                   ),
//                   const CustomProgressIndicatorWidget(),
//                 ],
//               ),
//             );
//           }
//           //print("^^^^^^^^^^^^^^^^^^ErrorString in Courses_Screen 3: ${_topicStore.errorString}");
//           if (_topicStore.errorString.isNotEmpty ||
//               _topicStore.topicList == null ||
//               _topicStore.lecturesAreLearningList == null ||
//               _topicStore.levelList == null) {
//             return Center(
//               child: Text(_topicStore.errorString,
//                   style: const TextStyle(color: Colors.red)),
//             );
//           }
//           //print("build In CoursesScreen+++++++++++++++++++++++++++++++");
//           return SingleChildScrollView(
//             physics: const ClampingScrollPhysics(),
//             child: Padding(
//                 padding: const EdgeInsets.all(8.0),
//                 child: Column(
//                   crossAxisAlignment: CrossAxisAlignment.center,
//                   children: [
//                     //Cover Image Introduction
//                     const CoverImageWidget(),
//                     const SizedBox(height: 15),
//                     //Topics Grid
//                     GridView.builder(
//                       padding: EdgeInsets.zero,
//                       physics: const NeverScrollableScrollPhysics(),
//                       shrinkWrap: true,
//                       gridDelegate:
//                           const SliverGridDelegateWithFixedCrossAxisCount(
//                         crossAxisCount: 3,
//                         mainAxisSpacing: 0,
//                         crossAxisSpacing: 0,
//                         childAspectRatio: 1,
//                         mainAxisExtent: 100, // set the height of each item
//                       ),
//                       itemCount: _topicStore.topicList!.topics.length,
//                       itemBuilder: (context, index) {
//                         return TopicItem(
//                           topic: _topicStore.topicList!.topics[index],
//                         );
//                       },
//                     ),

//                     const SizedBox(height: 15),
//                     //Text "Chủ đề đang học"
//                     Padding(
//                       padding: const EdgeInsets.symmetric(horizontal: 10),
//                       child: Row(
//                         mainAxisAlignment: MainAxisAlignment.start,
//                         children: [
//                           Image.asset(
//                             'assets/images/fire.png',
//                             width: 20,
//                             height: 28,
//                             fit: BoxFit.contain,
//                           ),
//                           const SizedBox(
//                             width: 10,
//                           ),
//                           Text(
//                             "Chủ đề đang học",
//                             style: Theme.of(context)
//                                 .textTheme
//                                 .subTitle
//                                 .copyWith(
//                                     color:
//                                         ColorsStandards.textColorInBackground2),
//                           )
//                         ],
//                       ),
//                     ),
//                     const SizedBox(height: 10),
//                     //Lectures is learning

//                     Column(
//                       children: _topicStore.lecturesAreLearningList!.lectures
//                           .map((lecture) {
//                         return LectureItem(lecture: lecture);
//                       }).toList(),
//                     )
//                   ],
//                 )),
//           );
//         },
//       ),
//     );
//   }
// }