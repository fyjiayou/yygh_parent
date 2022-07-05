package com.fystart.yygh.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> findChildrenDataById(Long id);

    void exportDict(HttpServletResponse response);

    void importDictData(MultipartFile file);
}
