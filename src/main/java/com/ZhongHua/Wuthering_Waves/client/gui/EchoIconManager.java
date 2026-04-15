package com.ZhongHua.Wuthering_Waves.client.gui;

import net.minecraft.resources.ResourceLocation;
import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;

public class EchoIconManager
{
    private static final ResourceLocation DEFAULT_ICON = new ResourceLocation(
            Wuthering_WavesMod.MOD_ID, "textures/echo/unknown.png");

    /**
     * 根据声骸名称获取图标路径
     * 支持中英文名称映射
     */
    public static ResourceLocation getIcon(String echoName)
    {
        if (echoName == null) return DEFAULT_ICON;

        // 中文名称映射（你可以根据需要扩展）
        String fileName = switch (echoName)
        {
            case "无冠者" -> "crownless_echo";
            case "刺玫菇（稚形）" -> "baby_roseshroom_echo";
            case "湮灭棱镜" -> "havoc_prism_echo";
            case "刺玫菇" -> "roseshroom_echo";
            case "暗鬃狼" -> "havoc_dreadmane_echo";

            default -> "unknown";
        };

        return new ResourceLocation(Wuthering_WavesMod.MOD_ID,
                "textures/echo/" + fileName + ".png");
    }
}