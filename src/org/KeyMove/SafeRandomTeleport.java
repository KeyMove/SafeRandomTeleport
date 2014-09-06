/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.KeyMove;

import java.io.File;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class SafeRandomTeleport extends JavaPlugin{
    String 触发字符串="[随机传送]";
    int 最大范围=1500;
    int 最小范围=-1500;
    YamlConfiguration 配置文件;
    List<Location> 传送点缓存=new ArrayList<>();
    public class 事件监听器 implements Listener{
        @EventHandler
        public void 玩家右键牌子事件(PlayerInteractEvent 事件){
            out.print("右键");
            if(事件.getAction()!=Action.RIGHT_CLICK_BLOCK)//如果不是右键点击方块则退出
                return;
            out.print("是右键方块");
            if(事件.getClickedBlock().getType()!=Material.WALL_SIGN)//如果被点击的方块不是牌子则退出
                return;
            out.print("是牌子");
            Sign 牌子=(Sign)事件.getClickedBlock().getState();//把方块类型转换为牌子
            String 牌子上的字符串[]=牌子.getLines();//获取牌子上的字符串
            out.print(Arrays.toString(牌子上的字符串));
            if(!牌子上的字符串[0].equals("§2"+触发字符串))//检测字符串是否符合规格
                return;
            Player 玩家=事件.getPlayer();
            玩家.teleport(寻找合适的传送点(牌子.getLocation()));
            /*
            if(传送点缓存.isEmpty())
            {
                玩家.sendMessage("未找到合适的落脚点");
                return;
            }
            玩家.teleport(传送点缓存.get(0));//传送到玩家到已缓存的传送点
            传送点缓存.remove(0);*/
        }
        @EventHandler
        public void 检测放置随机传送牌子的玩家是否OP(SignChangeEvent 事件){//检测玩家放置牌子的事件
            out.print("玩家牌子");
            out.print(事件.getBlock().toString());
            Block 牌子=事件.getBlock();//获取被放置的牌子
            out.print(触发字符串);
            out.print(事件.getLine(0));
            if(!事件.getLine(0).equals(触发字符串))//如果牌子内容跟字符串不同则退出
                return;
            out.print("相同");
            if(!事件.getPlayer().isOp())//如果牌子内容一样而且还不是OP
            {
                事件.setLine(0, "没有权限");
                事件.setLine(1, "没有权限");
                事件.setLine(2, "没有权限");
                事件.setLine(3, "没有权限");
            }
            事件.setLine(0,"§2"+事件.getLine(0));
            事件.getPlayer().sendMessage("§6[随机传送]:§4随机传送已创建");
        }
    }
    
    public Location 寻找合适的传送点(Location 起始点){
            Location 目标点;
            World 世界=起始点.getWorld();
            Block 方块;
            for(int loop=0;loop<10;loop++){
                起始点.setX(起始点.getX()+getRandomInt(最小范围,最大范围));
                起始点.setZ(起始点.getZ()+getRandomInt(最小范围,最大范围));
                起始点.setY(128);
                目标点=起始点;
                int air=0;
                for(int i=0;i<128;i++)
                {
                    方块=世界.getBlockAt(目标点);
                    if(方块.getType()!=Material.AIR)
                    {
                        if(air>=2&&方块.getType()!=Material.LAVA)
                        {
                            return 目标点;
                        }
                        else
                        {
                            air=0;
                        }
                    }
                    else
                    {
                        air++;
                    }
                    目标点.setY(目标点.getY()-1);
                }
            }
            起始点.setX(起始点.getX()+getRandomInt(最小范围,最大范围));
                起始点.setZ(起始点.getZ()+getRandomInt(最小范围,最大范围));
                起始点.setY(128);
                目标点=起始点;
                int air=0;
                for(int i=0;i<128;i++)
                {
                    方块=世界.getBlockAt(目标点);
                    if(方块.getType()!=Material.AIR)
                    {
                        if(air>=2&&方块.getType()!=Material.LAVA)
                        {
                            return 目标点;
                        }
                        else
                        {
                            air=0;
                        }
                    }
                    else
                    {
                        air++;
                    }
                    目标点.setY(目标点.getY()-1);
                }
                if(air>=127){
                    目标点.setY(125);
                    世界.getBlockAt(目标点).setType(Material.STONE);
                    目标点.setY(126);
                    世界.getBlockAt(目标点).setType(Material.AIR);
                    目标点.setY(127);
                    世界.getBlockAt(目标点).setType(Material.AIR);
                    目标点.setY(126);
                }
            return 目标点;
        }
    
    public int getRandomInt(int a, int b) {  
        if (a > b || a < 0)  
            return -1;  
        // 下面两种形式等价  
        // return a + (int) (new Random().nextDouble() * (b - a + 1));  
        return a + (int) (Math.random() * (b - a + 1));  
    }
    
    public static void main(String[] args) {
        
    }

    public void 加载配置文件(){
        File 配置=new File(getDataFolder(),"config.yml");
        if(!配置.exists())
        {
            this.saveDefaultConfig();
            配置=new File(getDataFolder(),"config.yml");
        }
        配置文件=YamlConfiguration.loadConfiguration(配置);
        List<World> l=getServer().getWorlds();
        out.print(l);
        触发字符串=配置文件.getString("牌子第一行");
        out.print("初始化字符串"+触发字符串);
    }
    
    @Override
    public void onEnable() {
        加载配置文件();
        getServer().getPluginManager().registerEvents(new 事件监听器(), this);
        out.print("安全的随机传送插件已经加载");
    }
    
}
