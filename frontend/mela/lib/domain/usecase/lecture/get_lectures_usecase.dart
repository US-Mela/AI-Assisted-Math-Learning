import 'dart:async';

import '../../../core/domain/usecase/use_case.dart';
import '../../entity/lecture/lecture_list.dart';
import '../../repository/lecture/lecture_repository.dart';


class GetLecturesUsecase extends UseCase<LectureList, int> {
  final LectureRepository _lectureRepository;

  GetLecturesUsecase(this._lectureRepository);
  
  @override
  Future<LectureList> call({required int params}) {
    return _lectureRepository.getLectures(params);
  }


}