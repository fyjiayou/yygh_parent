package com.fystart.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {
        //设置excel文件路径和文件名称
        String fileName = "C:\\Users\\冯粤\\Desktop\\01.xlsx";

        List<UserData> userDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserData userData = new UserData(i,"zhangsan"+i);
            userDataList.add(userData);
        }

        //调用方法实现写操作
        EasyExcel.write(fileName,UserData.class).sheet("用户信息").doWrite(userDataList);

    }
}
