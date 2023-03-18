package ru.will0376.cases.server;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.gson.annotations.SerializedName;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;

import java.util.List;

import static com.google.common.collect.Iterables.transform;

@GradleSideOnly(GradleSide.SERVER)
public class JCase {
	@SerializedName("name")
	private String name;
	@SerializedName("texture")
	private String texture;
	@SerializedName("items")
	private List<JCaseItem> items;
	private double totalWeight;
	private String itemsOutput;

	public JCase() {
		this.totalWeight = -1.0;
		this.itemsOutput = null;
	}

	public String getName() {
		return this.name;
	}

	public String getTexture() {
		return this.texture;
	}

	public List<JCaseItem> getItems() {
		return this.items;
	}

	public JCaseItem getRandomItem() {
		if (this.getTotalWeight() == -1.0) {
			this.totalWeight = this.calculateWeight();
		}
		int randomIndex = -1;
		double random = Math.random() * this.getTotalWeight();
		for (int index = 0; index < this.getItems().size(); ++index) {
			random -= this.getItems().get(index).getChance();
			if (random <= 0.0) {
				randomIndex = index;
				break;
			}
		}
		return this.getItems().get(randomIndex);
	}

	public double getTotalWeight() {
		return this.totalWeight;
	}

	private double calculateWeight() {
		double totalWeight = 0.0;
		for (final JCaseItem item : this.getItems()) {
			totalWeight += item.getChance();
		}
		return totalWeight;
	}

	public String format(final int available) {
		return String.format("%s,%s,%s", this.name, available, this.texture);
	}

	public String formatItems() {
		if (this.itemsOutput == null) {
			this.itemsOutput = Joiner.on(",").join(transform((Iterable<JCaseItem>) this.items, (Function<? super JCaseItem, ?>) caseItem -> {
				if (caseItem != null) {
					return caseItem.format();
				}
				return null;
			}));
		}
		return this.itemsOutput;
	}
}
