/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.KeyMove;

import java.io.File;
import static java.lang.System.out;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class SafeRandomTeleport extends JavaPlugin{
    String 触发字符串="[随机传送]";
    int 最大范围=3500;
    int 最小范围=-3500;
    Map<Player, Location> 玩家缓存点=new HashMap<>();
    YamlConfiguration 配置文件;
    public class 事件监听器 implements Listener{
        @EventHandler
        public void 玩家右键牌子事件(PlayerInteractEvent 事件){
            if(事件.getAction()!=Action.RIGHT_CLICK_BLOCK)//如果不是右键点击方块则退出
                return;
            if(事件.getClickedBlock().getType()!=Material.WALL_SIGN)//如果被点击的方块不是牌子则退出
                return;
            Sign 牌子=(Sign)事件.getClickedBlock().getState();//把方块类型转换为牌子
            String 牌子上的字符串[]=牌子.getLines();//获取牌子上的字符串
            if(!牌子上的字符串[0].equals("§2"+触发字符串))//检测字符串是否符合规格
                return;
            Player 玩家=事件.getPlayer();
            玩家.setNoDamageTicks(100);//设置免伤时间 用于检测是否卡墙里
            Location 点=寻找合适的传送点(牌子.getLocation());
            玩家缓存点.put(玩家, 点);
            玩家.teleport(点);//传送到合适的地点
        }
        @EventHandler
        public void 检测放置随机传送牌子的玩家是否OP(SignChangeEvent 事件){//检测玩家放置牌子的事件
            Block 牌子=事件.getBlock();//获取被放置的牌子
            if(!事件.getLine(0).equals(触发字符串))//如果牌子内容跟字符串不同则退出
                return;
            if(!事件.getPlayer().isOp())//如果牌子内容一样而且还不是OP
            {
                事件.setLine(0, "没有权限");
                事件.setLine(1, "没有权限");
                事件.setLine(2, "没有权限");
                事件.setLine(3, "没有权限");
                return;
            }
            事件.setLine(0,"§2"+事件.getLine(0));
            事件.getPlayer().sendMessage("§6[随机传送]:§4随机传送已创建");
        }
        @EventHandler
        public void 检测是否卡墙里(EntityDamageEvent 事件){//实体收到伤害事件
            if(!(事件.getEntity() instanceof Player))//如果受到伤害的实体不是玩家则退出
                return;
            //检测伤害类型 不等于 窒息 岩浆 则退出
            if(事件.getCause()!=EntityDamageEvent.DamageCause.SUFFOCATION&&事件.getCause()!=EntityDamageEvent.DamageCause.LAVA)
                return;
            Player 玩家=(Player)事件.getEntity();
            if(玩家.getNoDamageTicks()==0)//如果免伤时间到期退出
                return;
            玩家.teleport(玩家缓存点.get(玩家));//确定是卡墙里 重置位置
            事件.setCancelled(true);
        }
        @EventHandler
        public void 玩家破坏牌子(BlockBreakEvent 事件){//玩家破坏方块事件
            if(事件.getBlock().getType()!=Material.WALL_SIGN)//如果破坏的方块不是牌子则退出
                return;
            Sign 牌子=(Sign)事件.getBlock().getState();//获取牌子数据
            if(!牌子.getLine(0).equals("§2"+触发字符串))//如果牌子第一行不匹配则退出
                return;
            if(事件.getPlayer().hasPermission("op"))//检测破坏的玩家是否有OP权限
            {
                事件.getPlayer().sendMessage("§6[随机传送]:§4随机传送已移除");//对OP发送提示信息
                return;
            }
            事件.setCancelled(true);//没有OP权限取消事件
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
                            目标点.setY(目标点.getY()+1.5);
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
                            目标点.setY(目标点.getY()+1.5);
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
        if (a > b)  
            return -1;  
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
        触发字符串=配置文件.getString("牌子第一行");
        最大范围=配置文件.getInt("默认最大坐标");
        最小范围=配置文件.getInt("默认最小坐标");
        if(触发字符串.length()==0||(最大范围==0&&最小范围==0))//配置文件加载错误 重新创建配置文件
        {
            配置.delete();
            out.print("配置文件错误");
            this.saveDefaultConfig();
            配置文件=YamlConfiguration.loadConfiguration(new File(getDataFolder(),"config.yml"));
            触发字符串="[随机传送]";
            最大范围=3500;
            最小范围=-3500;
        }
    }
    @Override
    public void onEnable() {//插件加载
        加载配置文件();//加载配置文件
        getServer().getPluginManager().registerEvents(new 事件监听器(), this);//注册事件监听器
        out.print("安全的随机传送插件已经加载");//输出提示信息
    }

    @Override
    public boolean onCommand(CommandSender 命令发送者, Command 命令, String label, String[] 参数列表) {//重载命令响应函数
        if(!命令发送者.hasPermission("op"))//检测发送者是否有OP权限
            return false;
        if(参数列表.length==0)//
        {
            命令发送者.sendMessage("§6[随机传送] /sftp Reload - 重载插件");
            命令发送者.sendMessage("§6[随机传送] /sftp info   - 插件参数");
            return true;
        }
        switch(参数列表[0]){
            case "reload":
                加载配置文件();
                命令发送者.sendMessage("§6[随机传送] 重载完毕");
                break;
            case "info":
                命令发送者.sendMessage("§6[随机传送] 检测名称:"+触发字符串+"最大坐标:"+最大范围+"最小坐标:"+最小范围);
                break;
        }
        return false;
    }
    
}
