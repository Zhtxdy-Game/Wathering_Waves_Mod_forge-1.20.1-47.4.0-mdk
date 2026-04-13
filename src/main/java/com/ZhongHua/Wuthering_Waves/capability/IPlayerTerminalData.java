package com.ZhongHua.Wuthering_Waves.capability;

import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public interface IPlayerTerminalData
{
    List<EchoInstance> getEchoList();
    void setEchoList(List<EchoInstance> list);
    boolean addEcho(EchoInstance instance);
    boolean removeEcho(int index);
    int getCurrentCount();
    int getMaxCapacity();
    void setMaxCapacity(int capacity);
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
    // 装备槽位相关
    List<EchoInstance> getEquippedEchoes();          // 返回5个槽位的列表（可能包含null）
    void equipEcho(int slot, EchoInstance echo);    // 装备声骸到指定槽位
    void unequipEcho(int slot);                     // 卸下指定槽位
    //属性加成相关
    EchoAttributeCache getAttributeCache();
    void recalculateAttributes(ServerPlayer player);  // 服务端调用，重新计算并应用属性

    int getTotalEquippedCost();
    int getMaxTotalCost();  // 返回上限，例如 12
    //升级声骸相关
    EchoInstance getEchoById(UUID id);
    boolean upgradeEcho(ServerPlayer player, EchoInstance echo);
    void upgradeEchoToMax(ServerPlayer player, EchoInstance echo, boolean confirmed);

}