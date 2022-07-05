package com.fystart.yygh.controller;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fystart.yygh.common.execption.YyghException;
import com.fystart.yygh.common.result.Result;
import com.fystart.yygh.service.HospitalSetService;
import com.fystart.yygh.serviceutil.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询医院设置表所有信息
     * @return
     */
    @ApiOperation("查询所有医院设置")
    @GetMapping("/getAllHospital")
    public Result getAllHospital(){
        List<HospitalSet> list = hospitalSetService.list();

        return Result.ok(list);
    }

    /**
     * 删除医院设置(逻辑删除)
     * @param id
     * @return
     */
    @ApiOperation("删除医院设置")
    @DeleteMapping("/deleteHospSet/{id}")
    public Result deleteHospSet(@PathVariable("id") String id){
//        System.out.println(id);
        long aLong = Long.parseLong(id);
        boolean flag = hospitalSetService.removeById(aLong);

        return flag ? Result.ok() : Result.fail();
    }

    /**
     * 分页条件查询
     * @param current 当前页
     * @param limit 每页显示的数量
     * @param hospitalQueryVo 查询的条件 (有可能每页分页条件)
     * @return
     */
    @ApiOperation("分页条件查询")
    @PostMapping("/pageQuery/{current}/{limit}")
    public Result pageQuery(@PathVariable("current") Long current,
                            @PathVariable("limit") Long limit,
                            @RequestBody(required = false) HospitalQueryVo hospitalQueryVo){

        Page<HospitalSet> page = new Page<>(current,limit);

        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();

        String hosname = hospitalQueryVo.getHosname();
        String hoscode = hospitalQueryVo.getHoscode();

        if (!StringUtils.isEmpty(hosname)){
            queryWrapper.like("hosname",hosname);
        }
        if (!StringUtils.isEmpty(hoscode)){
            queryWrapper.eq("hoscode",hoscode);
        }

        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, queryWrapper);

        return Result.ok(hospitalSetPage);
    }

    /**
     * 添加医院设置
     * @param hospitalSet
     * @return
     */
    @ApiOperation("添加医院设置")
    @PostMapping("/addHospitalSet")
    public Result addHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态 1.可以使用  0.不能使用
        hospitalSet.setStatus(1);

        //签名密钥 MD5加密算法
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));

        boolean flag = hospitalSetService.save(hospitalSet);

        return flag ? Result.ok() : Result.fail();
    }

    /**
     * 根据id获取医院设置
     * @param id 传入的id
     * @return
     */
    @ApiOperation("根据id获取医院设置")
    @GetMapping("/getHospSetById/{id}")
    public Result getHospSetById(@PathVariable("id") Long id){

//        try{
//            int i = 10/0;
//        }catch (Exception e){
//            throw new YyghException("失败",201);
//        }

        HospitalSet hospitalSetServiceById = hospitalSetService.getById(id);

        return Result.ok(hospitalSetServiceById);
    }

    @ApiOperation("修改医院设置")
    @PostMapping("/updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);

        return flag ? Result.ok() : Result.fail();
    }


    @ApiOperation("批量删除医院设置")
    @DeleteMapping("/deleteBatchHospSet")
    public Result deleteBatchHospSet(@RequestBody List<Long> ids){
        System.out.println(ids);
        hospitalSetService.removeBatchByIds(ids);

        return Result.ok();
    }

    @ApiOperation("医院设置锁定和解锁")
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable("id") Long id,
                                  @PathVariable("status") String status){

        HospitalSet hospitalSetServiceById = hospitalSetService.getById(id);

        hospitalSetServiceById.setStatus(Integer.parseInt(status));

        hospitalSetService.updateById(hospitalSetServiceById);

        return Result.ok();
    }

    @ApiOperation("发送签名密钥")
    @PostMapping("/sendMsgKey/{id}")
    public Result sendMsgKey(@PathVariable("id") Long id){
        HospitalSet hospitalSetServiceById = hospitalSetService.getById(id);

        String signKey = hospitalSetServiceById.getSignKey();
        String hoscode = hospitalSetServiceById.getHoscode();

        //TODO 发送短信
        return Result.ok();
    }
}
