package com.kokomi.web.job;

import cn.hutool.core.util.StrUtil;
import com.kokomi.web.manager.CosManager;
import com.kokomi.web.mapper.GeneratorMapper;
import com.kokomi.web.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ClearCosJobHandler {
    @Resource
    private CosManager cosManager;
    @Resource
    private GeneratorMapper generatorMapper;

    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() throws Exception {
        log.info("clearCosJobHandler start");
        cosManager.deleteDir("/generator_make_template/");
        List<Generator> generatorList = generatorMapper.listDeletedGenerator();
        List<String> keyList = generatorList.stream()
                .map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                .map(distPath -> distPath.substring(1))
                .collect(Collectors.toList());
        cosManager.deleteObjects(keyList);
        log.info("clearCosJobHandler end");
    }
}
