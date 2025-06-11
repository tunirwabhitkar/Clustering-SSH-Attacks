# 🛡️ Clustering SSH Attacks using KMeans 🚀

A machine learning project to **identify and group SSH attack patterns** using the **KMeans clustering algorithm** in **Java + WEKA**.  
Transform raw network data into meaningful clusters to understand the nature of SSH attacks.

---

## 🎯 Objective

🔍 **Automatically detect patterns in SSH attack data** by clustering similar behaviors extracted from `.pcap` network files.  
This helps in early detection and classification of attack types such as brute-force, scanning, and abnormal sessions.

---

## 🧠 Features

✅ Preprocessed `.pcap` files into `.csv` for structured analysis  
✅ Applied **KMeans clustering** to detect similarities and group attacks  
✅ Used **WEKA** and **Java** for model training and evaluation  
✅ Visual and interpretable results for security insights

---

## 🛠️ Technologies Used

| Tool             | Description                          |
|------------------|--------------------------------------|
| ☕ Java           | Programming language                 |
| 📦 WEKA          | Machine learning toolkit              |
| 🧪 tshark/Scapy  | Packet capture preprocessing         |
| 🐙 Git + GitHub  | Version control and collaboration    |

---

## 📁 Project Structure

```plaintext
Clustering-SSH-Attacks/
├── Data/            # Preprocessed CSVs from PCAP files
├── src/             # Java source code
├── wekaAnalysis/    # WEKA model files and analysis
├── eclipse/         # Eclipse project configs (if used)
└── README.md        # You're here!
