package edu.wust.qrz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.wust.qrz.entity.system.Dictionary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {
}
