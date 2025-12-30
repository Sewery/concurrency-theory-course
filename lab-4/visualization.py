import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

def create_visualizations(df: pd.DataFrame):
    sns.set_theme(style="whitegrid")
    fig, axes = plt.subplots(3, 1, figsize=(15, 15), constrained_layout=True)

    # 1. Box Plot (Wykres pudełkowy)
    ax1 = axes[0]
    sns.boxplot(x='Implementacja', y='CzasOczekiwania', data=df, ax=ax1)
    # Uproszczone stylowanie
    ax1.set_title('Czasy oczekiwania dla różnych implementacji')
    ax1.set_xlabel('Implementacja')
    ax1.set_ylabel('Czas oczekiwania (ms)')
    ax1.set_xticklabels(ax1.get_xticklabels(), rotation=45)

    # Dodawanie wartości median
    medians = df.groupby('Implementacja')['CzasOczekiwania'].median()
    plot_categories = [t.get_text() for t in ax1.get_xticklabels()]
    for i, category in enumerate(plot_categories):
        median_val = medians.loc[category]
        ax1.text(
            i, median_val, f'{median_val:.1f}',
            horizontalalignment='center', verticalalignment='bottom'
        )

    # 2. Violin Plot (Wykres skrzypcowy)
    ax2 = axes[1]
    sns.violinplot(x='Implementacja', y='CzasOczekiwania', data=df, ax=ax2)
    # Uproszczone stylowanie
    ax2.set_title('Rozkład czasów oczekiwania')
    ax2.set_xlabel('Implementacja')
    ax2.set_ylabel('Czas oczekiwania (ms)')
    ax2.set_xticklabels(ax2.get_xticklabels(), rotation=45)

    # 3. Bar Plot (Wykres słupkowy)
    ax3 = axes[2]
    meals_stats = df.groupby('Implementacja')['PosilkiZjedzone'].agg(['mean', 'std']).reset_index()

    bars = ax3.bar(meals_stats['Implementacja'], meals_stats['mean'])
    ax3.errorbar(
        meals_stats['Implementacja'], meals_stats['mean'],
        yerr=meals_stats['std'], fmt='none', color='black', capsize=5
    )
    # Uproszczone stylowanie
    ax3.set_title('Średnia liczba posiłków dla różnych implementacji')
    ax3.set_xlabel('Implementacja')
    ax3.set_ylabel('Średnia liczba posiłków')
    ax3.set_xticklabels(ax3.get_xticklabels(), rotation=45)

    for bar in bars:
        height = bar.get_height()
        ax3.text(
            bar.get_x() + bar.get_width() / 2., height,
            f'{height:.1f}', ha='center', va='bottom'
        )

    plt.savefig('philosophers_analysis.png', dpi=300, bbox_inches='tight')

def main():
    # Usunięto blok try...except
    df = pd.read_csv("wyniki.csv")

    create_visualizations(df)
if __name__ == "__main__":
    main()

