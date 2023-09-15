package com.xin.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.project.model.vo.UserInterfaceInfoAnalysisVo;
import com.xin.xinapicommon.model.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 86166
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2023-08-06 21:00:23
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfoAnalysisVo> listTopInterfaceInfo(@Param("num") int num);

}




