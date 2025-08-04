package edu.wust.qrz.vo.content;

import edu.wust.qrz.entity.content.Teachplan;
import edu.wust.qrz.entity.content.TeachplanMedia;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeachPlanVO extends Teachplan {
    private TeachplanMedia teachplanMedia; // 关联媒体信息
    private List<TeachPlanVO> children = new ArrayList<>(); // 子节点列表

    public TeachPlanVO() {
        super();
        this.children = new ArrayList<>();
    }
}
