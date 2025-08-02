package edu.wust.qrz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.wust.qrz.common.Result;
import edu.wust.qrz.entity.system.Dictionary;

public interface DictionaryService extends IService<Dictionary> {
    Result getAllDictionary();
}
