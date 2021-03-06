package games.strategy.triplea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.triplea.java.collections.CollectionUtils;
import org.triplea.java.collections.IntegerMap;
import org.triplea.util.Tuple;

import com.google.common.collect.ImmutableMap;

import games.strategy.engine.data.Change;
import games.strategy.engine.data.CompositeChange;
import games.strategy.engine.data.GameData;
import games.strategy.engine.data.MutableProperty;
import games.strategy.engine.data.PlayerId;
import games.strategy.engine.data.Territory;
import games.strategy.engine.data.Unit;
import games.strategy.engine.data.UnitType;
import games.strategy.engine.data.changefactory.ChangeFactory;
import games.strategy.triplea.attachments.TechAbilityAttachment;
import games.strategy.triplea.attachments.TerritoryAttachment;
import games.strategy.triplea.attachments.UnitAttachment;
import games.strategy.triplea.delegate.Matches;

/**
 * Extended unit for triplea games.
 *
 * <p>
 * As with all game data components, changes made to this unit must be made through a Change instance. Calling setters
 * on this directly will not serialize the changes across the network.
 * </p>
 */
public class TripleAUnit extends Unit {
  public static final String TRANSPORTED_BY = "transportedBy";
  public static final String UNLOADED = "unloaded";
  public static final String LOADED_THIS_TURN = "wasLoadedThisTurn";
  public static final String UNLOADED_TO = "unloadedTo";
  public static final String UNLOADED_IN_COMBAT_PHASE = "wasUnloadedInCombatPhase";
  public static final String ALREADY_MOVED = "alreadyMoved";
  public static final String BONUS_MOVEMENT = "bonusMovement";
  public static final String SUBMERGED = "submerged";
  public static final String WAS_IN_COMBAT = "wasInCombat";
  public static final String LOADED_AFTER_COMBAT = "wasLoadedAfterCombat";
  public static final String UNLOADED_AMPHIBIOUS = "wasAmphibious";
  public static final String ORIGINATED_FROM = "originatedFrom";
  public static final String WAS_SCRAMBLED = "wasScrambled";
  public static final String MAX_SCRAMBLE_COUNT = "maxScrambleCount";
  public static final String WAS_IN_AIR_BATTLE = "wasInAirBattle";
  public static final String LAUNCHED = "launched";
  public static final String AIRBORNE = "airborne";
  public static final String CHARGED_FLAT_FUEL_COST = "chargedFlatFuelCost";
  private static final long serialVersionUID = 8811372406957115036L;

  // the transport that is currently transporting us
  private TripleAUnit transportedBy = null;
  // the units we have unloaded this turn
  private List<Unit> unloaded = Collections.emptyList();
  // was this unit loaded this turn?
  private boolean wasLoadedThisTurn = false;
  // the territory this unit was unloaded to this turn
  private Territory unloadedTo = null;
  // was this unit unloaded in combat phase this turn?
  private boolean wasUnloadedInCombatPhase = false;
  // movement used this turn
  private int alreadyMoved = 0;
  // movement used this turn
  private int bonusMovement = 0;
  // amount of damage unit has sustained
  private int unitDamage = 0;
  // is this submarine submerged
  private boolean submerged = false;
  // original owner of this unit
  private PlayerId originalOwner = null;
  // Was this unit in combat
  private boolean wasInCombat = false;
  private boolean wasLoadedAfterCombat = false;
  private boolean wasAmphibious = false;
  // the territory this unit started in (for use with scrambling)
  private Territory originatedFrom = null;
  private boolean wasScrambled = false;
  private int maxScrambleCount = -1;
  private boolean wasInAirBattle = false;
  private boolean disabled = false;
  // the number of airborne units launched by this unit this turn
  private int launched = 0;
  // was this unit airborne and launched this turn
  private boolean airborne = false;
  // was charged flat fuel cost already this turn
  private boolean chargedFlatFuelCost = false;

  public TripleAUnit(final UnitType type, final PlayerId owner, final GameData data) {
    super(type, owner, data);
  }

  public static TripleAUnit get(final Unit u) {
    return (TripleAUnit) u;
  }

  public TripleAUnit getTransportedBy() {
    return transportedBy;
  }

  private void setTransportedBy(final TripleAUnit transportedBy) {
    this.transportedBy = transportedBy;
  }

  /**
   * This is a very slow method because it checks all territories on the map. Try not to use this method if possible.
   */
  public List<Unit> getTransporting() {
    // we don't store the units we are transporting
    // rather we look at the transported by property of units
    for (final Territory t : getData().getMap()) {
      // find the territory this transport is in
      if (t.getUnitCollection().contains(this)) {
        return getTransporting(t.getUnitCollection());
      }
    }
    return Collections.emptyList();
  }

  public List<Unit> getTransporting(final Collection<Unit> transportedUnitsPossible) {
    // we don't store the units we are transporting
    // rather we look at the transported by property of units
    return CollectionUtils.getMatches(transportedUnitsPossible, o -> equals(get(o).getTransportedBy()));
  }

  public List<Unit> getUnloaded() {
    return unloaded;
  }

  private void setUnloaded(final List<Unit> unloaded) {
    if (unloaded == null || unloaded.isEmpty()) {
      this.unloaded = Collections.emptyList();
    } else {
      this.unloaded = new ArrayList<>(unloaded);
    }
  }

  public boolean getWasLoadedThisTurn() {
    return wasLoadedThisTurn;
  }

  private void setWasLoadedThisTurn(final boolean value) {
    wasLoadedThisTurn = value;
  }

  public Territory getUnloadedTo() {
    return unloadedTo;
  }

  private void setUnloadedTo(final Territory unloadedTo) {
    this.unloadedTo = unloadedTo;
  }

  public Territory getOriginatedFrom() {
    return originatedFrom;
  }

  private void setOriginatedFrom(final Territory t) {
    originatedFrom = t;
  }

  public boolean getWasUnloadedInCombatPhase() {
    return wasUnloadedInCombatPhase;
  }

  private void setWasUnloadedInCombatPhase(final boolean value) {
    wasUnloadedInCombatPhase = value;
  }

  public int getAlreadyMoved() {
    return alreadyMoved;
  }

  public void setAlreadyMoved(final int alreadyMoved) {
    this.alreadyMoved = alreadyMoved;
  }

  private void setBonusMovement(final int bonusMovement) {
    this.bonusMovement = bonusMovement;
  }

  public int getBonusMovement() {
    return bonusMovement;
  }

  /**
   * Does not account for any movement already made. Generally equal to UnitType movement
   */
  public int getMaxMovementAllowed() {
    return Math.max(0, bonusMovement + UnitAttachment.get(getType()).getMovement(getOwner()));
  }

  public int getMovementLeft() {
    return Math.max(0, UnitAttachment.get(getType()).getMovement(getOwner()) + bonusMovement - alreadyMoved);
  }

  /**
   * Returns a tuple whose first element indicates the minimum movement remaining for the specified collection of units,
   * and whose second element indicates the maximum movement remaining for the specified collection of units.
   */
  public static Tuple<Integer, Integer> getMinAndMaxMovementLeft(final Collection<Unit> units) {
    int min = 100000;
    int max = 0;
    for (final Unit u : units) {
      final int left = ((TripleAUnit) u).getMovementLeft();
      if (left > max) {
        max = left;
      }
      if (left < min) {
        min = left;
      }
    }
    if (max < min) {
      min = max;
    }
    return Tuple.of(min, max);
  }

  public int getUnitDamage() {
    return unitDamage;
  }

  public void setUnitDamage(final int unitDamage) {
    this.unitDamage = unitDamage;
  }

  public boolean getSubmerged() {
    return submerged;
  }

  public void setSubmerged(final boolean submerged) {
    this.submerged = submerged;
  }

  public PlayerId getOriginalOwner() {
    return originalOwner;
  }

  private void setOriginalOwner(final PlayerId originalOwner) {
    this.originalOwner = originalOwner;
  }

  public boolean getWasInCombat() {
    return wasInCombat;
  }

  private void setWasInCombat(final boolean value) {
    wasInCombat = value;
  }

  public boolean getWasScrambled() {
    return wasScrambled;
  }

  private void setWasScrambled(final boolean value) {
    wasScrambled = value;
  }

  public int getMaxScrambleCount() {
    return maxScrambleCount;
  }

  private void setMaxScrambleCount(final int value) {
    maxScrambleCount = value;
  }

  public int getLaunched() {
    return launched;
  }

  private void setLaunched(final int value) {
    launched = value;
  }

  public boolean getAirborne() {
    return airborne;
  }

  private void setAirborne(final boolean value) {
    airborne = value;
  }

  public boolean getChargedFlatFuelCost() {
    return chargedFlatFuelCost;
  }

  private void setChargedFlatFuelCost(final boolean value) {
    chargedFlatFuelCost = value;
  }

  private void setWasInAirBattle(final boolean value) {
    wasInAirBattle = value;
  }

  public boolean getWasInAirBattle() {
    return wasInAirBattle;
  }

  public boolean getWasLoadedAfterCombat() {
    return wasLoadedAfterCombat;
  }

  private void setWasLoadedAfterCombat(final boolean value) {
    wasLoadedAfterCombat = value;
  }

  public boolean getWasAmphibious() {
    return wasAmphibious;
  }

  private void setWasAmphibious(final boolean value) {
    wasAmphibious = value;
  }

  public boolean getDisabled() {
    return disabled;
  }

  private void setDisabled(final boolean value) {
    disabled = value;
  }

  /**
   * How much more damage can this unit take?
   * Will return 0 if the unit cannot be damaged, or is at max damage.
   */
  public int getHowMuchMoreDamageCanThisUnitTake(final Unit u, final Territory t) {
    if (!Matches.unitCanBeDamaged().test(u)) {
      return 0;
    }
    final TripleAUnit taUnit = (TripleAUnit) u;
    return Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(u.getData())
        ? Math.max(0, getHowMuchDamageCanThisUnitTakeTotal(u, t) - taUnit.getUnitDamage())
        : Integer.MAX_VALUE;
  }

  /**
   * How much damage is the max this unit can take, accounting for territory, etc.
   * Will return -1 if the unit is of the type that cannot be damaged
   */
  public int getHowMuchDamageCanThisUnitTakeTotal(final Unit u, final Territory t) {
    if (!Matches.unitCanBeDamaged().test(u)) {
      return -1;
    }
    final UnitAttachment ua = UnitAttachment.get(u.getType());
    final int territoryUnitProduction = TerritoryAttachment.getUnitProduction(t);
    if (Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(u.getData())) {
      if (ua.getMaxDamage() <= 0) {
        // factories may or may not have max damage set, so we must still determine here
        // assume that if maxDamage <= 0, then the max damage must be based on the territory value
        // can use "production" or "unitProduction"
        return territoryUnitProduction * 2;
      }

      if (Matches.unitCanProduceUnits().test(u)) {
        // can use "production" or "unitProduction"
        return (ua.getCanProduceXUnits() < 0) ? territoryUnitProduction * ua.getMaxDamage() : ua.getMaxDamage();
      }

      return ua.getMaxDamage();
    }

    return Integer.MAX_VALUE;
  }

  public int getHowMuchCanThisUnitBeRepaired(final Unit u, final Territory t) {
    return Math.max(0,
        (this.getHowMuchDamageCanThisUnitTakeTotal(u, t) - this.getHowMuchMoreDamageCanThisUnitTake(u, t)));
  }

  public static int getProductionPotentialOfTerritory(final Collection<Unit> unitsAtStartOfStepInTerritory,
      final Territory producer, final PlayerId player, final GameData data, final boolean accountForDamage,
      final boolean mathMaxZero) {
    return getHowMuchCanUnitProduce(
        getBiggestProducer(unitsAtStartOfStepInTerritory, producer, player, data, accountForDamage), producer, player,
        data, accountForDamage, mathMaxZero);
  }

  /**
   * Returns the unit from the specified collection that has the largest production capacity within the specified
   * territory.
   *
   * @param accountForDamage {@code true} if the production capacity should account for unit damage; otherwise
   *        {@code false}.
   */
  public static Unit getBiggestProducer(final Collection<Unit> units, final Territory producer, final PlayerId player,
      final GameData data, final boolean accountForDamage) {
    final Predicate<Unit> factoryMatch = Matches.unitIsOwnedAndIsFactoryOrCanProduceUnits(player)
        .and(Matches.unitIsBeingTransported().negate())
        .and(producer.isWater()
            ? Matches.unitIsLand().negate()
            : Matches.unitIsSea().negate());
    final Collection<Unit> factories = CollectionUtils.getMatches(units, factoryMatch);
    if (factories.isEmpty()) {
      return null;
    }
    final IntegerMap<Unit> productionPotential = new IntegerMap<>();
    Unit highestUnit = factories.iterator().next();
    int highestCapacity = Integer.MIN_VALUE;
    for (final Unit u : factories) {
      final int capacity = getHowMuchCanUnitProduce(u, producer, player, data, accountForDamage, false);
      productionPotential.put(u, capacity);
      if (capacity > highestCapacity) {
        highestCapacity = capacity;
        highestUnit = u;
      }
    }
    return highestUnit;
  }

  /**
   * Returns the production capacity for the specified unit within the specified territory.
   *
   * @param accountForDamage {@code true} if the production capacity should account for unit damage; otherwise
   *        {@code false}.
   * @param mathMaxZero {@code true} if a negative production capacity should be rounded to zero; {@code false} to allow
   *        a negative production capacity.
   */
  public static int getHowMuchCanUnitProduce(final Unit u, final Territory producer, final PlayerId player,
      final GameData data, final boolean accountForDamage, final boolean mathMaxZero) {
    if (u == null) {
      return 0;
    }
    if (!Matches.unitCanProduceUnits().test(u)) {
      return 0;
    }
    final UnitAttachment ua = UnitAttachment.get(u.getType());
    final TripleAUnit taUnit = (TripleAUnit) u;
    final TerritoryAttachment ta = TerritoryAttachment.get(producer);
    int territoryProduction = 0;
    int territoryUnitProduction = 0;
    if (ta != null) {
      territoryProduction = ta.getProduction();
      territoryUnitProduction = ta.getUnitProduction();
    }
    int productionCapacity;
    if (accountForDamage) {
      if (Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(data)) {
        if (ua.getCanProduceXUnits() < 0) {
          // we could use territoryUnitProduction OR
          // territoryProduction if we wanted to, however we should
          // change damage to be based on whichever we choose.
          productionCapacity = territoryUnitProduction - taUnit.getUnitDamage();
        } else {
          productionCapacity = ua.getCanProduceXUnits() - taUnit.getUnitDamage();
        }
      } else {
        productionCapacity = territoryProduction;
        if (productionCapacity < 1) {
          productionCapacity =
              (Properties.getWW2V2(data) || Properties.getWW2V3(data)) ? 0
                  : 1;
        }
      }
    } else {
      if (ua.getCanProduceXUnits() < 0
          && !Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(data)) {
        productionCapacity = territoryProduction;
      } else if (ua.getCanProduceXUnits() < 0
          && Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(data)) {
        productionCapacity = territoryUnitProduction;
      } else {
        productionCapacity = ua.getCanProduceXUnits();
      }
      if (productionCapacity < 1
          && !Properties.getDamageFromBombingDoneToUnitsInsteadOfTerritories(data)) {
        productionCapacity =
            (Properties.getWW2V2(data) || Properties.getWW2V3(data)) ? 0
                : 1;
      }
    }
    // Increase production if have industrial technology
    if (territoryProduction >= TechAbilityAttachment.getMinimumTerritoryValueForProductionBonus(player, data)) {
      productionCapacity += TechAbilityAttachment.getProductionBonus(u.getType(), player, data);
    }
    return mathMaxZero ? Math.max(0, productionCapacity) : productionCapacity;
  }

  /**
   * Currently made for translating unit damage from one unit to another unit. Will adjust damage to be within max
   * damage for the new units.
   *
   * @return change for unit's properties
   */
  public static Change translateAttributesToOtherUnits(final Unit unitGivingAttributes,
      final Collection<Unit> unitsThatWillGetAttributes, final Territory t) {
    final CompositeChange changes = new CompositeChange();
    // must look for hits, unitDamage,
    final TripleAUnit taUnit = (TripleAUnit) unitGivingAttributes;
    final int combatDamage = taUnit.getHits();
    final IntegerMap<Unit> hits = new IntegerMap<>();
    if (combatDamage > 0) {
      for (final Unit u : unitsThatWillGetAttributes) {
        final int maxHitPoints = UnitAttachment.get(u.getType()).getHitPoints();
        final int transferDamage = Math.min(combatDamage, maxHitPoints - 1);
        if (transferDamage <= 0) {
          continue;
        }
        hits.put(u, transferDamage);
      }
    }
    if (!hits.isEmpty()) {
      changes.add(ChangeFactory.unitsHit(hits));
    }
    final int unitDamage = taUnit.getUnitDamage();
    final IntegerMap<Unit> damageMap = new IntegerMap<>();
    if (unitDamage > 0) {
      for (final Unit u : unitsThatWillGetAttributes) {
        final TripleAUnit taNew = (TripleAUnit) u;
        final int maxDamage = taNew.getHowMuchDamageCanThisUnitTakeTotal(u, t);
        final int transferDamage = Math.max(0, Math.min(unitDamage, maxDamage));
        if (transferDamage <= 0) {
          continue;
        }
        damageMap.put(u, transferDamage);
      }
    }
    if (!damageMap.isEmpty()) {
      changes.add(ChangeFactory.bombingUnitDamage(damageMap));
    }
    return changes;
  }

  @Override
  public Map<String, MutableProperty<?>> getPropertyMap() {
    return ImmutableMap.<String, MutableProperty<?>>builder()
        .putAll(super.getPropertyMap())
        .put("transportedBy",
            MutableProperty.ofSimple(
                this::setTransportedBy,
                this::getTransportedBy))
        .put("unloaded",
            MutableProperty.ofSimple(
                this::setUnloaded,
                this::getUnloaded))
        .put("wasLoadedThisTurn",
            MutableProperty.ofSimple(
                this::setWasLoadedThisTurn,
                this::getWasLoadedThisTurn))
        .put("unloadedTo",
            MutableProperty.ofSimple(
                this::setUnloadedTo,
                this::getUnloadedTo))
        .put("wasUnloadedInCombatPhase",
            MutableProperty.ofSimple(
                this::setWasUnloadedInCombatPhase,
                this::getWasUnloadedInCombatPhase))
        .put("alreadyMoved",
            MutableProperty.ofSimple(
                this::setAlreadyMoved,
                this::getAlreadyMoved))
        .put("bonusMovement",
            MutableProperty.ofSimple(
                this::setBonusMovement,
                this::getBonusMovement))
        .put("unitDamage",
            MutableProperty.ofSimple(
                this::setUnitDamage,
                this::getUnitDamage))
        .put("submerged",
            MutableProperty.ofSimple(
                this::setSubmerged,
                this::getSubmerged))
        .put("originalOwner",
            MutableProperty.ofSimple(
                this::setOriginalOwner,
                this::getOriginalOwner))
        .put("wasInCombat",
            MutableProperty.ofSimple(
                this::setWasInCombat,
                this::getWasInCombat))
        .put("wasLoadedAfterCombat",
            MutableProperty.ofSimple(
                this::setWasLoadedAfterCombat,
                this::getWasLoadedAfterCombat))
        .put("wasAmphibious",
            MutableProperty.ofSimple(
                this::setWasAmphibious,
                this::getWasAmphibious))
        .put("originatedFrom",
            MutableProperty.ofSimple(
                this::setOriginatedFrom,
                this::getOriginatedFrom))
        .put("wasScrambled",
            MutableProperty.ofSimple(
                this::setWasScrambled,
                this::getWasScrambled))
        .put("maxScrambleCount",
            MutableProperty.ofSimple(
                this::setMaxScrambleCount,
                this::getMaxScrambleCount))
        .put("wasInAirBattle",
            MutableProperty.ofSimple(
                this::setWasInAirBattle,
                this::getWasInAirBattle))
        .put("disabled",
            MutableProperty.ofSimple(
                this::setDisabled,
                this::getDisabled))
        .put("launched",
            MutableProperty.ofSimple(
                this::setLaunched,
                this::getLaunched))
        .put("airborne",
            MutableProperty.ofSimple(
                this::setAirborne,
                this::getAirborne))
        .put("chargedFlatFuelCost",
            MutableProperty.ofSimple(
                this::setChargedFlatFuelCost,
                this::getChargedFlatFuelCost))
        .build();
  }
}
