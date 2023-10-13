import datasets
import directories

if __name__ == "__main__":
    print(directories.DATA_DIR)
    print(directories.PLOTS_DIR)
    reversions_dataset = datasets.load_reversions_with_images("USDJPY", "M30")