import 'package:flutter/material.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/di/service_locator.dart';
import 'package:mela/presentation/home_screen/store/level_store/level_store.dart';
import 'package:mela/presentation/question/store/question_store.dart';
import 'package:mela/utils/routes/routes.dart';

import '../../../domain/entity/exercise/exercise.dart';

class ExerciseItem extends StatelessWidget {
  final _levelStore= getIt<LevelStore>();
  final _questionStore = getIt<QuestionStore>();
  final Exercise currentExercise;

  ExerciseItem({
    required this.currentExercise,
  });

  @override
  Widget build(BuildContext context) {
    String topicName =
        _levelStore.getTopicNameById(currentExercise.topicId);
    String levelName =
        _levelStore.getLevelNameById(currentExercise.levelId);
    return GestureDetector(
      onTap: () {
        _questionStore.setQuestionsUid(currentExercise.exerciseId);
        Navigator.pushNamed(context, Routes.question);
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 16, left: 16, right: 16),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Row(
          children: [
            // Image + completed questions/total questions
            Expanded(
              flex: 1,
              child: Column(
                children: [
                  Image.asset(currentExercise.imageExercisePath,
                      width: 60, height: 60),
                  const SizedBox(height: 8),
                  currentExercise.bestResult != null
                      ? Text(
                          '${currentExercise.bestResult!.totalCorrectAnswers} / ${currentExercise.totalQuestions}',
                          style: Theme.of(context)
                              .textTheme
                              .miniCaption
                              .copyWith(color: Colors.black),
                        )
                      : const SizedBox.shrink()
                ],
              ),
            ),
            const SizedBox(width: 10),

            Expanded(
              flex: 2,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Topic name + level name
                  Text(
                    '$topicName - $levelName',
                    style: Theme.of(context)
                        .textTheme
                        .subTitle
                        .copyWith(color: Colors.orange),
                  ),

                  const SizedBox(height: 4),
                  // Exercise name
                  Text(
                    currentExercise.exerciseName,
                    style: Theme.of(context)
                        .textTheme
                        .subTitle
                        .copyWith(color: Colors.black),
                  ),

                  const SizedBox(height: 8),
                  // Number of questions + type of questions
                  Text(
                    '${currentExercise.totalQuestions} câu | ${currentExercise.typeQuestion}',
                    style: Theme.of(context)
                        .textTheme
                        .normal
                        .copyWith(color: Colors.black),
                  ),

                  const SizedBox(width: 16),

                  //Status of exercise
                  Row(
                    children: [
                      Text(
                        'Trạng thái:   ',
                        style: Theme.of(context)
                            .textTheme
                            .normal
                            .copyWith(color: Colors.black),
                      ),
                      currentExercise.bestResult != null
                          ? Text(
                              currentExercise.statusExercise
                                  ? "Đạt"
                                  : "Chưa đạt",
                              style:
                                  Theme.of(context).textTheme.normal.copyWith(
                                        color: currentExercise.statusExercise
                                            ? Colors.green
                                            : Colors.red,
                                      ),
                            )
                          : Text('Chưa làm',
                              style:
                                  Theme.of(context).textTheme.normal.copyWith(
                                        color: Colors.black,
                                      )),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(width: 6),
          ],
        ),
      ),
    );
  }
}
