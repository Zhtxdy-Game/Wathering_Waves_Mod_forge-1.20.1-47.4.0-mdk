package com.ZhongHua.Wuthering_Waves.capability;

import com.ZhongHua.Wuthering_Waves.Config;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.network.ModNetwork;
import com.ZhongHua.Wuthering_Waves.network.SyncTerminalDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerTerminalDataImpl implements IPlayerTerminalData
{
    private List<EchoInstance> echoList = new ArrayList<>();
    private List<EchoInstance> equippedEchoes = new ArrayList<>(Arrays.asList(null, null, null, null, null));
    // 不在字段初始化时读取配置，改为在需要时获取
    private int cachedMaxCapacity = -1;  // 缓存值，-1表示未加载

    public void syncToClient(ServerPlayer player)
    {
        // 方式1：如果 ModNetwork 中定义了 sendToPlayer 静态方法
        ModNetwork.sendToPlayer(new SyncTerminalDataPacket(this.serializeNBT()), player);

        // 方式2：直接使用 CHANNEL（需确保 ModNetwork.CHANNEL 是 public static）
        // ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
        //         new SyncTerminalDataPacket(this.serializeNBT()));
    }
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
                cachedMaxCapacity = 200; // 回退默认值
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
    public List<EchoInstance> getEquippedEchoes()
    {
        return equippedEchoes;
    }

    @Override
    public void equipEcho(int slot, EchoInstance echo)
    {
        if (slot >= 0 && slot < 5)
        {
            equippedEchoes.set(slot, echo);
        }
    }

    @Override
    public void unequipEcho(int slot) {
        if (slot >= 0 && slot < 5) {
            equippedEchoes.set(slot, null);
        }
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        // 保存 echoList
        ListTag listTag = new ListTag();
        for (EchoInstance echo : echoList) {
            listTag.add(echo.toNBT());
        }
        tag.put("EchoList", listTag);

        // 保存 equippedEchoes
        ListTag equipTag = new ListTag();
        for (EchoInstance echo : equippedEchoes)
        {
            if (echo != null) {
                equipTag.add(echo.toNBT());
            } else {
                equipTag.add(new CompoundTag());
            }
        }
        tag.put("EquippedEchoes", equipTag);
        tag.putInt("MaxCapacity", getMaxCapacity());
        return tag;
    }


    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        // 读取 echoList
        ListTag listTag = nbt.getList("EchoList", Tag.TAG_COMPOUND);
        List<EchoInstance> newList = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++)
        {
            CompoundTag entry = listTag.getCompound(i);
            EchoInstance echo = EchoInstance.fromNBT(entry);
            if (echo != null) newList.add(echo);
        }
        this.echoList = newList;

        // 读取 equippedEchoes
        ListTag equipTag = nbt.getList("EquippedEchoes", Tag.TAG_COMPOUND);
        List<EchoInstance> newEquip = new ArrayList<>();
        for (int i = 0; i < equipTag.size() && i < 5; i++)
        {
            CompoundTag entry = equipTag.getCompound(i);
            if (entry.isEmpty())
            {
                newEquip.add(null);
            } else
            {
                newEquip.add(EchoInstance.fromNBT(entry));
            }
        }
        while (newEquip.size() < 5) newEquip.add(null);
        this.equippedEchoes = newEquip;

        // 容量处理
        if (nbt.contains("MaxCapacity", Tag.TAG_INT))
        {
            this.cachedMaxCapacity = nbt.getInt("MaxCapacity");
        } else {
            this.cachedMaxCapacity = -1;
        }
        int max = getMaxCapacity();
        while (this.echoList.size() > max) {
            this.echoList.remove(this.echoList.size() - 1);
        }
    }

}