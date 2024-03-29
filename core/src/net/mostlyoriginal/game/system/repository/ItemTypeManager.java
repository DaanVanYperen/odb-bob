package net.mostlyoriginal.game.system.repository;

import com.artemis.E;
import com.artemis.ESubscription;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.manager.AssetManager;
import net.mostlyoriginal.game.component.Item;
import net.mostlyoriginal.game.component.ItemData;
import net.mostlyoriginal.game.component.ItemMetadata;
import net.mostlyoriginal.game.component.inventory.Inside;

/**
 * @author Daan van Yperen
 */
public class ItemTypeManager extends AssetManager<Item, ItemMetadata> {

    public ItemLibrary itemLibrary;
    private int rewardDropTotal = 0;
    private int desireDropTotal = 0;

    public ItemTypeManager() {
        super(Item.class, ItemMetadata.class);
    }

    @Override
    protected void setup(int entity, Item item, ItemMetadata itemMetadata) {
        // load metadata on item.
        if (item.type == null) throw new RuntimeException();
        itemMetadata.data = itemLibrary.getById(item.type);
        if (itemMetadata.data==null )
            throw new RuntimeException("Unknown type " + item.type);
        E e = E.E(entity);
        e
                .anim(itemMetadata.data.sprite)
                .walkable(itemMetadata.data.dryLand)
                .extensionPoint(itemMetadata.data.extensionPoint)
                .locked(itemMetadata.data.defaultLocked);
    }

    @All({Item.class})
    @Exclude(Inside.class)
    private ESubscription items;

    @Override
    protected void initialize() {
        super.initialize();
        final Json json = new Json();
        itemLibrary = json.fromJson(ItemLibrary.class, Gdx.files.internal("items.json"));
        for (ItemData item : itemLibrary.items) {
            rewardDropTotal += item.rewardChance;
            desireDropTotal += item.desireChance;
        }
    }

    public ItemData get(String type) {
        ItemData byId = itemLibrary.getById(type);
        if (byId == null) throw new RuntimeException("unknown item type " + type);
        return byId;
    }

    public String randomReward() {
        boolean valid = false;

        int target = MathUtils.random(0, rewardDropTotal - 1);
        for (ItemData item : itemLibrary.items) {
            target -= item.rewardChance;
            if (target <= 0) return item.id;
        }
        return null;
    }

    public String availableCovetedItemType() {
        for (E item : items) {
            final String type = item.itemType();
            if (itemLibrary.getById(type).coveted) {
                return type;
            }
        }
        return null;
    }

    public String randomDesire() {
        if (MathUtils.random(1, 100) < 10) {
            // fairly high chance people want a valuable item, if the player has it.
            String covetedItemType = availableCovetedItemType();
            if (covetedItemType != null) return covetedItemType;
        }

        int target = MathUtils.random(0, desireDropTotal - 1);
        for (ItemData item : itemLibrary.items) {
            target -= item.desireChance;
            if (target <= 0) return item.id;
        }
        return null;
    }

    public String substitute(String ingredient) {
        String identicalTo = get(ingredient).identicalTo;
        return identicalTo != null ? identicalTo : ingredient;
    }
}

