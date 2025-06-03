/*    */ package net.mcreator.createrecipesplusplus;
/*    */ 
/*    */ import java.util.AbstractMap;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.ConcurrentLinkedQueue;
/*    */ import java.util.function.BiConsumer;
/*    */ import java.util.function.Function;
/*    */ import java.util.function.Supplier;
/*    */ import net.minecraft.network.FriendlyByteBuf;
/*    */ import net.minecraft.resources.ResourceLocation;
/*    */ import net.minecraftforge.common.MinecraftForge;
/*    */ import net.minecraftforge.event.TickEvent;
/*    */ import net.minecraftforge.eventbus.api.IEventBus;
/*    */ import net.minecraftforge.eventbus.api.SubscribeEvent;
/*    */ import net.minecraftforge.fml.common.Mod;
/*    */ import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
/*    */ import net.minecraftforge.network.NetworkEvent;
/*    */ import net.minecraftforge.network.NetworkRegistry;
/*    */ import net.minecraftforge.network.simple.SimpleChannel;
/*    */ import org.apache.logging.log4j.LogManager;
/*    */ import org.apache.logging.log4j.Logger;
/*    */ 
/*    */ @Mod("create_recipes_plusplus")
/*    */ public class CreateRecipesMod
/*    */ {
/* 43 */   public static final Logger LOGGER = LogManager.getLogger(CreateRecipesMod.class);
/*    */   public static final String MODID = "create_recipes_plusplus";
/*    */   
/*    */   public CreateRecipesMod() {
/* 47 */     MinecraftForge.EVENT_BUS.register(this);
/* 48 */     IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
/*    */   }
/*    */ 
/*    */   
/*    */   private static final String PROTOCOL_VERSION = "1";
/* 53 */   public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation("create_recipes_plusplus", "create_recipes_plusplus"), () -> "1", "1"::equals, "1"::equals);
/* 54 */   private static int messageID = 0;
/*    */   
/*    */   public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
/* 57 */     PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
/* 58 */     messageID++;
/*    */   }
/*    */   
/* 61 */   private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
/*    */   
/*    */   public static void queueServerWork(int tick, Runnable action) {
/* 64 */     workQueue.add(new AbstractMap.SimpleEntry<>(action, Integer.valueOf(tick)));
/*    */   }
/*    */   
/*    */   @SubscribeEvent
/*    */   public void tick(TickEvent.ServerTickEvent event) {
/* 69 */     if (event.phase == TickEvent.Phase.END) {
/* 70 */       List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
/* 71 */       workQueue.forEach(work -> {
/*    */             work.setValue(Integer.valueOf(((Integer)work.getValue()).intValue() - 1));
/*    */             if (((Integer)work.getValue()).intValue() == 0)
/*    */               actions.add(work); 
/*    */           });
/* 76 */       actions.forEach(e -> ((Runnable)e.getKey()).run());
/* 77 */       workQueue.removeAll(actions);
/*    */     } 
/*    */   }
/*    */ }