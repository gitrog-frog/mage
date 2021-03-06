/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.t;

import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.ManacostVariableValue;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.BoostAllEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.repository.CardRepository;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author anonymous
 */
public class TribalUnity extends CardImpl {

    public TribalUnity(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.INSTANT},"{X}{2}{G}");

        // Creatures of the creature type of your choice get +X/+X until end of turn.
        this.getSpellAbility().addEffect(new TribalUnityEffect(new ManacostVariableValue()));
    }

    public TribalUnity(final TribalUnity card) {
        super(card);
    }

    @Override
    public TribalUnity copy() {
        return new TribalUnity(this);
    }
}



class TribalUnityEffect extends OneShotEffect {
    protected DynamicValue amount;

    public TribalUnityEffect(DynamicValue amount) {
        super(Outcome.UnboostCreature);
        staticText = "Creatures of the creature type of your choice get +X/+X until end of turn.";
        this.amount = amount;
    }

    public TribalUnityEffect(final TribalUnityEffect effect) {
        super(effect);
        this.amount = effect.amount;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        MageObject sourceObject = game.getObject(source.getSourceId());
        int boost = amount.calculate(game, source, this);
        if (player != null) {
            Choice typeChoice = new ChoiceImpl(true);
            typeChoice.setMessage("Choose a creature type:");
            typeChoice.setChoices(SubType.getCreatureTypes(false));
            while (!player.choose(outcome, typeChoice, game)) {
                if (!player.canRespond()) {
                    return false;
                }
            }
            if (typeChoice.getChoice() != null) {
                game.informPlayers(sourceObject.getLogName() + " chosen type: " + typeChoice.getChoice());
            }
            FilterCreaturePermanent filterCreaturePermanent = new FilterCreaturePermanent();
            filterCreaturePermanent.add(new SubtypePredicate(SubType.byDescription(typeChoice.getChoice())));
            game.addEffect(new BoostAllEffect(
                    boost, boost, Duration.EndOfTurn, filterCreaturePermanent, false), source);
            return true;
        }
        return false;
    }

    @Override
    public TribalUnityEffect copy() {
        return new TribalUnityEffect(this);
    }
}