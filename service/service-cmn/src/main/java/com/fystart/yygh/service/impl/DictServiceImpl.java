package com.fystart.yygh.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fystart.yygh.listener.DictListener;
import com.fystart.yygh.mapper.DictMapper;
import com.fystart.yygh.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {


    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findChildrenDataById(Long id) {

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("parent_id",id);

        List<Dict> dictList = baseMapper.selectList(queryWrapper);

        //向list集合每个dict对象中设置hasChildren
        List<Dict> newDictList = dictList.stream()
                .map(dict -> {
                    Long dictId = dict.getId();
                    boolean hasChildren = this.hasChildren(dictId);
                    dict.setHasChildren(hasChildren);
                    return dict;
                })
                .collect(Collectors.toList());

        return newDictList;
    }

    /**
     * 判断id下面是否有子节点
     * @param id
     * @return
     */
    private boolean hasChildren(Long id){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("parent_id",id);

        Long count = baseMapper.selectCount(queryWrapper);

        return count>0;
    }

    @Override
    public void exportDict(HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);

        //把Dict -> DictEeVo
        List<DictEeVo> dictEeVos = new ArrayList<>();
        for (Dict dict : dictList){
            DictEeVo dictEeVo = new DictEeVo();

            BeanUtils.copyProperties(dict,dictEeVo);

            dictEeVos.add(dictEeVo);
        }

        //调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictEeVos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = "dict",allEntries = true)
    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
