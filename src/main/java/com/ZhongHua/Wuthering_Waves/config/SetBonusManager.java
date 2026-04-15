package com.ZhongHua.Wuthering_Waves.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "wuthering_waves")
public class SetBonusManager implements ResourceManagerReloadListener
{
    private static final Map<String, EchoSetConfig> SETS = new HashMap<>();
    private static final Gson GSON = new Gson();

    // 根据声骸名称查询所属套装ID
    private static final Map<String, String> ECHO_TO_SET = new HashMap<>();

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(new SetBonusManager());
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        SETS.clear();
        ECHO_TO_SET.clear();

        // 加载所有套装配置
        var resources = manager.listResources("echo_sets", rl -> rl.getPath().endsWith(".json"));

        for (var entry : resources.entrySet())
        {
            try (var reader = new InputStreamReader(entry.getValue().open()))
            {
                EchoSetConfig config = GSON.fromJson(reader, EchoSetConfig.class);
                SETS.put(config.getId(), config);

                // 建立反向映射：声骸名 -> 套装ID
                for (String member : config.getMembers())
                {
                    ECHO_TO_SET.put(member, config.getId());
                }
            } catch (Exception e)
            {
                System.err.println("Failed to load echo set config: " + entry.getKey());
                e.printStackTrace();
            }
        }

        System.out.println("Loaded " + SETS.size() + " echo sets");
    }

    // 获取套装配置
    public static EchoSetConfig getSet(String setId)
    {
        return SETS.get(setId);
    }

    // 根据声骸名称获取所属套装ID（可能为null）
    public static String getSetIdByEcho(String echoName)
    {
        return ECHO_TO_SET.get(echoName);
    }

    // 获取所有套装配置（用于显示）
    public static Map<String, EchoSetConfig> getAllSets()
    {
        return new HashMap<>(SETS);
    }
}