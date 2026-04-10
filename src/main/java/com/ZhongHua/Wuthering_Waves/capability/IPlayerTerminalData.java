package com.ZhongHua.Wuthering_Waves.capability;

import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.nbt.CompoundTag;
import java.util.List;

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
}