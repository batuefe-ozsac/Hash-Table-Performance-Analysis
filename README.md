# News Article Search Engine & Hash Table Performance Analysis

This project implements a custom search engine utilizing dynamically growable Hash Tables developed from scratch in Java. Designed to index a large dataset of CNN news articles, the system allows for fast full-text searches and evaluates the performance of various hashing algorithms. 

## 📊 The Dataset & Scalability
The core architecture processes a dataset originally containing around 28.6K lines of CNN news articles from 2011 to 2022. 
* **Note on Repository Data:** Due to GitHub's file size constraints, a scaled-down sample dataset is included in this repository. However, the system is fully optimized to read, parse, and efficiently index the original, massive CSV dataset without performance degradation.

## 🚀 Engine Features
* **Advanced Data Preprocessing:** Fetches articles, strips punctuation, and meticulously removes stop words (using a predefined list) to index only meaningful content.
* **Intelligent Indexing:** Utilizes multiple hash maps for distinct purposes: article data storage, the main indexing structure mapping words to documents, and tracking word frequencies.
* **Search Functionalities:** * **By ID:** Retrieves specific article details.
  * **By Text:** Splits search queries into tokens, searches the index, and ranks the top 5 most relevant articles based on word occurrence.
* **Dynamic Resizing:** The hash tables automatically double in size when the maximum load factor is reached.

## 🛠️ Algorithmic Implementation
To ensure optimal performance, the project implements and benchmarks multiple approaches:

**1. Hash Functions Evaluated:**
* **Simple Summation Function (SSF):** Generates hash codes by summing character values directly.
* **Polynomial Accumulation Function (PAF):** Applies Horner's rule to prevent overflow while calculating polynomial hash values.

**2. Collision Resolution Techniques:**
* **Linear Probing (LP):** Handles collisions by placing items in the next circularly available cell.
* **Double Hashing (DH):** Utilizes a secondary hash function for robust collision management.

## 📈 Performance Benchmarking
The system runs rigorous search tests (sequentially searching 1000 predefined words) to populate a comprehensive performance matrix. It monitors and compares:
* Collision counts across different algorithm combinations.
* Total indexing time required to load the articles.
* Average search time under different load factors (50% and 80%).
