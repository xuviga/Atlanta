package ru.imine.client.fancychat.image;

public interface IChatRenderable
{
	int getWidthInSpaces();

	int getTextureId();

	void disposeTexture();

	int getWidth();

	int getHeight();

	int getTexCoordX();

	int getTexCoordY();

	default int render(int x, int y)
	{
		return render(x, y, getWidth(), getHeight(), 0xFFFFFF);
	}

	default int render(int x, int y, int color)
	{
		return render(x, y, getWidth(), getHeight(), color);
	}

	default int render(int x, int y, int width, int height)
	{
		return render(x, y, width, height, 0xFFFFFF);
	}

	int render(int x, int y, int width, int height, int color);
}