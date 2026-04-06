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
}