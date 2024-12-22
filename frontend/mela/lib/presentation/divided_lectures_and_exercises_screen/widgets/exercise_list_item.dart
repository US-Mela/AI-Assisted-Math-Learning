import 'package:flutter/material.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/presentation/divided_lectures_and_exercises_screen/store/exercise_store.dart';

import '../../../di/service_locator.dart';
import 'exercise_item.dart';

class ExerciseListItem extends StatelessWidget {
  final ExerciseStore _exerciseStore = getIt<ExerciseStore>();
  ExerciseListItem();
  @override
  Widget build(BuildContext context) {
    if (_exerciseStore.exerciseList!.exercises.isEmpty) {
      return Center(
        child: Text(
          "Hiện tại chưa có bài tập ở đây",
          style: Theme.of(context)
              .textTheme
              .subTitle
              .copyWith(color: Theme.of(context).colorScheme.primary),
        ),
      );
    }
    return ListView.builder(
      itemCount: _exerciseStore.exerciseList!.exercises.length,
      itemBuilder: (context, index) {
        return ExerciseItem(
            currentExercise: _exerciseStore.exerciseList!.exercises[index]);
      },
    );
  }
}
