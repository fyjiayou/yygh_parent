package com.fystart.yygh.controller;

import com.atguigu.yygh.model.cmn.Dict;
import com.fystart.yygh.common.result.Result;
import com.fystart.yygh.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 根据数据id查询子数据列表
     * @param id
     * @return
     */
    @ApiOperation("根据数据id查询子数据列表")
    @GetMapping("/findChildrenData/{id}")
    public Result findChildrenData(@PathVariable("id") Long id){
        List<Dict> dictList = dictService.findChildrenDataById(id);

        return Result.ok(dictList);
    }

    /**
     * 导出数据字典接口 == 下载
     * @return
     */
    @ApiOperation("导出数据字典")
    @GetMapping("/exportDict")
    public void exportDict(HttpServletResponse response){
        dictService.exportDict(response);
    }

    @ApiOperation("导入数据字典")
    @PostMapping("/importDict")
    public Result importDict(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();
    }


}
