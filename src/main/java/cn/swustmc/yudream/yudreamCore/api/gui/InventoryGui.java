package cn.swustmc.yudream.yudreamCore.api.gui;

import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryGui extends InventoryHolder {
    default void onOpen(InventoryOpenEvent event) {

    }

    default void onClose(InventoryCloseEvent event) {

    }

    default void onMoveItem(InventoryMoveItemEvent event) {

    }

    default void onDrag(InventoryDragEvent event) {

    }

    default void onClick(InventoryClickEvent event) {

    }

    InventoryGui setData(Object... args);

}
