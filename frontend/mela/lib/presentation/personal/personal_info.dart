import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/presentation/personal/store/personal_store.dart';
import 'package:mela/presentation/personal/widgets/delete_account_dialog.dart';
import '../../di/service_locator.dart';
import '../../themes/default/colors_standards.dart';
import 'package:flutter_mobx/flutter_mobx.dart';

import '../../utils/routes/routes.dart';
import 'edit_screens/edit_birthdate_screen.dart';
import 'edit_screens/edit_name_screen.dart';

class PersonalInfo extends StatefulWidget {
  final String name;
  final String email;
  final String dob;
  final String? imageUrl;

  const PersonalInfo({
    super.key,
    required this.name,
    required this.email,
    required this.dob,
    this.imageUrl,
  });

  @override
  _PersonalInfoState createState() => _PersonalInfoState();
}

class _PersonalInfoState extends State<PersonalInfo> {
  File? _image; //for uploaded image
  late ImageProvider _profileImage; //can be _image or can be passed from imageURL
  bool defaultImage = false; //flag for default avatar image
  final PersonalStore _personalStore = getIt<PersonalStore>();

  final ImagePicker _picker = ImagePicker(); //image picker

  @override
  void initState() {
    super.initState();
    _setProfileImage();
  }

  void _navigateToEditName() {
    Navigator.push(
      context,
      PageRouteBuilder(
        pageBuilder: (context, animation, secondaryAnimation) => EditNameScreen(
          name: widget.name,
          email: widget.email,
          dob: widget.dob,
          imageUrl: widget.imageUrl,
        ),
        transitionsBuilder: (context, animation, secondaryAnimation, child) {
          const begin = Offset(1.0, 0.0); // Bắt đầu từ bên phải
          const end = Offset.zero; // Kết thúc ở vị trí gốc
          const curve = Curves.easeInOut;

          var tween = Tween(begin: begin, end: end).chain(CurveTween(curve: curve));
          var offsetAnimation = animation.drive(tween);

          return SlideTransition(
            position: offsetAnimation,
            child: child,
          );
        },
      ),
    );
  }

  void _navigateToEditEmail() { //not yet available for email editing
    // Navigator.of(context).push(
    //   MaterialPageRoute(
    //     builder: (context) => EditEmailScreen(email: widget.email),
    //   ),
    // );
  }

  void _navigateToEditBirthdate() {
    Navigator.push(
      context,
      PageRouteBuilder(
        pageBuilder: (context, animation, secondaryAnimation) => EditBirthdateScreen(
          name: widget.name,
          email: widget.email,
          dob: widget.dob,
          imageUrl: widget.imageUrl,
        ),
        transitionsBuilder: (context, animation, secondaryAnimation, child) {
          const begin = Offset(1.0, 0.0); // Bắt đầu từ bên phải
          const end = Offset.zero; // Kết thúc ở vị trí gốc
          const curve = Curves.easeInOut;

          var tween = Tween(begin: begin, end: end).chain(CurveTween(curve: curve));
          var offsetAnimation = animation.drive(tween);

          return SlideTransition(
            position: offsetAnimation,
            child: child,
          );
        },
      ),
    );
  }

  // Hàm hiển thị hộp thoại xác nhận xóa tài khoản
  void _showDeleteAccountDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return DeleteAccountConfirmationDialog(
          onDelete: () async {
            try {
              final success = await _personalStore.deleteAccount();
              if (success) {
                Navigator.of(context).pushNamedAndRemoveUntil(
                    Routes.loginScreen, (route) => false
                );
              }
            } catch (e) {
              if (e is DioException) {
                if (e.response?.statusCode == 401) {
                  Navigator.of(context).pushNamedAndRemoveUntil(
                      Routes.loginScreen, (route) => false
                  );
                }
              }
              print(e.toString());
            }
          },
          onCancel: () {
            Navigator.of(context).pop(); // Đóng hộp thoại
          },
        );
      },
    );
  }

  void _setProfileImage() {
    if (_image != null) {
      _profileImage = FileImage(_image!);
      defaultImage = false;
    } else if (widget.imageUrl != null && widget.imageUrl!.isNotEmpty && !defaultImage) {
      _profileImage = NetworkImage(widget.imageUrl!);
      defaultImage = false;
    } else {
      _profileImage = const AssetImage('assets/icons/default_profile_pic.png');
      defaultImage = true;
    }
  }

  Future<void> _pickImage(ImageSource source) async {
    final pickedFile = await _picker.pickImage(source: source);
    final temp = _image;
    if (pickedFile != null) {
      try {
        _showSnackBar("Đang tải ảnh lên...");
        await _personalStore.updateImage(File(pickedFile.path));
        await _personalStore.getUserInfo();
        setState(() {
          _image = File(pickedFile.path);
          _setProfileImage();
          //
        });
        _showSnackBar("Ảnh đại diện đã được cập nhật...");
      } catch (e) {
        _showSnackBar("Không thể tải ảnh của bạn!");
        setState(() {
          _image = temp;
          _setProfileImage();
          //
        });
      }
    }
  }

  Future<void> _removeImage() async {
    final temp = _image;
    setState(() {
      _image = null;
      defaultImage = true;
      _setProfileImage();
    });
    try {
      await _personalStore.updateImage(File(""));
      await _personalStore.getUserInfo();
    } catch (e) {
      _showSnackBar("Không thể xóa ảnh của bạn!");
      setState(() {
        _image = temp;
        _setProfileImage();
        //
      });
    }
  }

  void _showImagePickerOptions() {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.camera_alt),
              title: const Text("Chụp ảnh mới"),
              onTap: () {
                Navigator.pop(context);
                _pickImage(ImageSource.camera);
              },
            ),
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text("Chọn ảnh từ thư viện"),
              onTap: () {
                Navigator.pop(context);
                _pickImage(ImageSource.gallery);
              },
            ),
            // if (_image != null || !defaultImage)
            //   ListTile(
            //     leading: const Icon(Icons.delete, color: Colors.red),
            //     title: const Text("Xóa ảnh đại diện", style: TextStyle(color: Colors.red)),
            //     onTap: () {
            //       Navigator.pop(context);
            //       _removeImage();
            //     },
            //   ),
          ],
        );
      },
    );
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorsStandards.AppBackgroundColor,
      appBar: AppBar(
        title: const Text("Thông tin cá nhân"),
        backgroundColor: ColorsStandards.AppBackgroundColor,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.of(context).pop();
          },
        ),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20.0),
          child: Container(
            height: double.infinity,
            width: double.infinity,
            child: Column(
              children: [
                // Avatar and Name at the top
                // Fixed position for Avatar and Name
                Container(
                  height: 160.0,
                  padding: const EdgeInsets.symmetric(vertical: 10.0),
                  alignment: Alignment.center,
                  child: Stack(
                    children: [
                      Column(
                        children: [
                          ClipRRect(
                            borderRadius: BorderRadius.circular(30.0),
                            child: Container(
                              width: 100.0,
                              height: 100.0,
                              decoration: BoxDecoration(
                                image: DecorationImage(
                                  image: _profileImage,
                                  fit: BoxFit.cover,
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(height: 5.0),
                          Observer(builder: (_) => const Text("")),
                        ],
                      ),
                      Positioned(
                        bottom: 15,
                        right: -3,
                        child: IconButton(
                          icon: Image.asset(
                            "assets/icons/upload_profile_pic.png",
                            width: 30,
                            height: 30,
                          ),
                          onPressed: _showImagePickerOptions, //_showImagePickerOptions,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 10.0), // Spacing before the list
                Expanded(
                  child: SingleChildScrollView(
                    child: Column(
                      children: [
                        _buildListTile(
                            context,
                            "Tên học viên",
                            widget.name,
                            _navigateToEditName
                            , Theme.of(context).colorScheme.textInBg1
                        ),
                        _buildListTile(
                            context,
                            "Email",
                            widget.email,
                                () {}
                            , Theme.of(context).colorScheme.textInBg2
                        ),
                        _buildListTile(
                            context,
                            "Ngày sinh",
                            widget.dob,
                            _navigateToEditBirthdate
                            , Theme.of(context).colorScheme.textInBg1
                        ),
                        _buildListTile(
                            context,
                            "Xóa tài khoản",
                            "",
                            _showDeleteAccountDialog,
                            Colors.red
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildListTile(BuildContext context, String title, String content, VoidCallback onTap, Color color) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 5.0),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8.0),
        boxShadow: const [
          BoxShadow(
            color: Colors.black26,
            blurRadius: 4.0,
            offset: Offset(0, 2),
          ),
        ],
      ),
      child: Padding(
        padding: const EdgeInsets.only(right: 0.0, left: 1.0),
        child: ListTile(
          leading: Text(
              title,
              style: Theme.of(context).textTheme.subTitle
                  .copyWith(color: color)
          ),
          trailing: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                content,
                style: Theme.of(context)
                    .textTheme
                    .questionStyle
                    .copyWith(color: color),
              ),
              //const SizedBox(width: 8.0),
              const Icon(Icons.arrow_forward_ios_sharp, size: 18.0),
            ],
          ),
          onTap: onTap,
        ),
      ),
    );
  }
}