//---Version 1:
import 'package:flutter/material.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/domain/entity/level/level.dart';
import 'package:mela/presentation/courses_screen/store/topic_store/topic_store.dart';
import 'package:mela/presentation/home_screen/store/level_store/level_store.dart';
import 'package:mela/presentation/lectures_in_topic_screen/store/lecture_store.dart';

import '../../../di/service_locator.dart';
import '../../../utils/routes/routes.dart';

class LevelItem extends StatelessWidget {
  final Level level;
  LevelItem({super.key, required this.level});

  final LectureStore _lectureStore = getIt<LectureStore>();
  final LevelStore _levelStore = getIt<LevelStore>();

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        // _lectureStore.setCurrentTopic(topic);
        // _levelStore.resetErrorString();
        // Navigator.of(context).pushNamed(Routes.allLecturesInTopicScreen);
      },
      child: Container(
        width: double.infinity,
        height: double.infinity,
        padding: const EdgeInsets.all(0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            //Logo topic
            SizedBox(
              width: 55,
              height: 55,
              child: Image.asset(
                level.levelImagePath.isEmpty
                    ? 'assets/images/grades/grade1.png'
                    : level.levelImagePath,
                fit: BoxFit.contain,
              ),
            ),
            const SizedBox(height: 4),

            //Name topic
            SizedBox(
              width: 75,
              child: Text(
                level.levelName,
                textAlign: TextAlign.center,
                maxLines: null,
                overflow: TextOverflow.visible,
                style: Theme.of(context).textTheme.miniCaption.copyWith(
                      color: Theme.of(context).colorScheme.primary,
                    ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}