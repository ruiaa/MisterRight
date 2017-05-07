1.	MainActivity 在退出时打开目标activity 清空栈
2.  注册 上传图片 token
3.  注册 第三步 返回错误
4.  把layout直接尺寸改为引用value
5.  全局keystorge保存MisterData
        0.  在登录时保存
        1.  在修改时保存 ：MineInfoFragment ResetPwd !!!要在返回成功时保存
        01. 统一在MisterApi处map保存
        2.  在退出时删除 ：AccountActivity
        3.  对于status 在变换时保存 MainActivity monitorStatus  MeetStatusChangeEvent
6.  ****
    联调后台掉包原因

7.  登录无返回inputPhone.setText("13430208680"); 有问题
8.  长按editinfo toolbar修改不可改资料
