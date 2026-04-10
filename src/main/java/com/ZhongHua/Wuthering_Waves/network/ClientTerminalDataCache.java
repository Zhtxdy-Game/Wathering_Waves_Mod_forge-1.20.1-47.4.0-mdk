package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.nbt.CompoundTag;
import java.util.ArrayList;
import java.util.List;

public class ClientTerminalDataCache
{
    private static List<EchoInstance> echoList = new ArrayList<>();
    private static List<EchoInstance> equippedEchoes = new ArrayList<>();
    private static int maxCapacity = 100;

    static
    {
        for (int i = 0; i < 5; i++) equippedEchoes.add(null);
    }

    public static void setData(CompoundTag nbt)
    {
        PlayerTerminalDataImpl dummy = new PlayerTerminalDataImpl();
        dummy.deserializeNBT(nbt);
        echoList = new ArrayList<>(dummy.getEchoList());
        equippedEchoes = new ArrayList<>(dummy.getEquippedEchoes());
        maxCapacity = dummy.getMaxCapacity();
    }

    public static List<EchoInstance> getEchoList() { return echoList; }
    public static List<EchoInstance> getEquippedEchoes() { return equippedEchoes; }
    public static int getMaxCapacity() { return maxCapacity; }
}