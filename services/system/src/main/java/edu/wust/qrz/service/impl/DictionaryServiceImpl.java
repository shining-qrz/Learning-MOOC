package edu.wust.qrz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.entity.system.Dictionary;
import edu.wust.qrz.mapper.DictionaryMapper;
import edu.wust.qrz.service.DictionaryService;
import edu.wust.qrz.vo.system.DictionaryItemValueVO;
import edu.wust.qrz.vo.system.DictionaryVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {
    @Override
    public Result getAllDictionary() {
        List<Dictionary> dictionaries = list();

        List<DictionaryVO> results = dictionaries.stream().map(dictionary -> {
            DictionaryVO dictionaryVO = new DictionaryVO();
            dictionaryVO.setId(dictionary.getId());
            dictionaryVO.setName(dictionary.getName());
            dictionaryVO.setCode(dictionary.getCode());

            String itemValues = dictionary.getItemValues();
            List<DictionaryItemValueVO> itemValueVOList = JSONUtil.toList(itemValues, DictionaryItemValueVO.class);
            dictionaryVO.setItemValues(itemValueVOList);

            return dictionaryVO;
        }).toList();

        return Result.ok(results);
    }
}
