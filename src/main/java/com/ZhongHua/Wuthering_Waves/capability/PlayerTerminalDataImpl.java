package com.ZhongHua.Wuthering_Waves.capability;

import com.ZhongHua.Wuthering_Waves.Config;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import java.util.ArrayList;
import java.util.List;

public class PlayerTerminalDataImpl implements IPlayerTerminalData
{
    private List<EchoInstance> echoList = new ArrayList<>();
    // 不在字段初始化时读取配置，改为在需要时获取
    private int cachedMaxCapacity = -1;  // 缓存值，-1表示未加载

    // 获取最大容量，延迟从 Config 读取
    private int getConfiguredMaxCapacity()
    {
        if (cachedMaxCapacity == -1)
        {
            // 确保 Config.SPEC 已加载，如果没有则使用默认值100
            try {
                cachedMaxCapacity = Config.TERMINAL_MAX_CAPACITY.get();
            } catch (Exception e)
            {
                cachedMaxCapacity = 100; // 回退默认值
            }
        }
        return cachedMaxCapacity;
    }

    @Override
    public int getMaxCapacity()
    {
        return Config.TERMINAL_MAX_CAPACITY.get();
    }

    @Override
    public void setMaxCapacity(int capacity)
    {
        // 允许动态设置容量，但注意此值不会持久化到 Config，仅用于运行时
        // 如果需要持久化，请在 NBT 中保存
        this.cachedMaxCapacity = capacity;
        // 截断超出容量的部分
        while (echoList.size() > capacity)
        {
            echoList.remove(echoList.size() - 1);
        }
    }

    @Override
    public List<EchoInstance> getEchoList()
    {
        return echoList;
    }

    @Override
    public void setEchoList(List<EchoInstance> list)
    {
        this.echoList = list != null ? list : new ArrayList<>();
        int max = getMaxCapacity();
        while (this.echoList.size() > max)
        {
            this.echoList.remove(this.echoList.size() - 1);
        }
    }

    @Override
    public boolean addEcho(EchoInstance instance)
    {
        if (echoList.size() >= getMaxCapacity())
        {
            return false;
        }
        echoList.add(instance);
        return true;
    }

    @Override
    public boolean removeEcho(int index)
    {
        if (index >= 0 && index < echoList.size())
        {
            echoList.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public int getCurrentCount()
    {
        return echoList.size();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (EchoInstance echo : echoList)
        {
            listTag.add(echo.toNBT());
        }
        tag.put("EchoList", listTag);
        // 保存当前运行时容量（可选）
        tag.putInt("MaxCapacity", getMaxCapacity());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        ListTag listTag = nbt.getList("EchoList", Tag.TAG_COMPOUND);
        List<EchoInstance> newList = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++)
        {
            CompoundTag entry = listTag.getCompound(i);
            EchoInstance echo = EchoInstance.fromNBT(entry);
            if (echo != null)
            {
                newList.add(echo);
            }
        }
        this.echoList = newList;
        if (nbt.contains("MaxCapacity", Tag.TAG_INT))
        {
            this.cachedMaxCapacity = nbt.getInt("MaxCapacity");
        } else {
            // 不设置缓存，下次调用 getMaxCapacity 时会从 Config 读取
            this.cachedMaxCapacity = -1;
        }
        int max = getMaxCapacity();
        while (this.echoList.size() > max)
        {
            this.echoList.remove(this.echoList.size() - 1);
        }
    }
}