import 'package:mela/data/network/constants/endpoints_const.dart';
import 'package:mela/data/network/dio_client.dart';
import 'package:mela/domain/entity/divided_lecture/divided_lecture_list.dart';
import 'package:mela/domain/entity/lecture/lecture_list.dart';
import 'package:mela/domain/entity/level/level_list.dart';

class LectureApi {
  DioClient _dioClient;
  LectureApi(this._dioClient);

  Future<LevelList> getLevels() async {
    print("================================ở getLevels API");
    final responseData = await _dioClient.get(EndpointsConst.getLevels);
    return LevelList.fromJson(responseData['data']);
  }

  Future<LectureList> getLectures(String topicId) async {
    print("================================ở getLectures API");
    final responseData = await _dioClient
        .get(EndpointsConst.getLectures, queryParameters: {'topicId': topicId});
    //print(responseData);
    return LectureList.fromJson(responseData['data']);
  }

  Future<LectureList> getLecturesAreLearning() async {
    print("================================ở getLectureAreLearning API");
    final responseData = await _dioClient.get(
        EndpointsConst.getLecturesAreLearning,
        queryParameters: {'size': 3});
    return LectureList.fromJson(responseData['data']);
  }

  Future<DividedLectureList> getDividedLectures(String lectureId) async {
    print("================================ở getDividedLectured API");
    final url =
        EndpointsConst.getDividedLectures.replaceAll(':lectureId', lectureId);
    final responseData =
        await _dioClient.get(url);

    //convert data
    List<dynamic> json = responseData['data'];
    json.forEach((dividedLecture) {
      dividedLecture['lectureId'] = lectureId;
    });
    // print("================================ResponseData LectureApi=====================with lectureId= $lectureId");
    // print("responseData: ${json}");

    return DividedLectureList.fromJson(json);
  }
}