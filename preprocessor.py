import os
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer
from nltk.stem import WordNetLemmatizer
from nltk import pos_tag
from nltk.corpus import wordnet as wn
from sklearn.feature_extraction.text import TfidfVectorizer
import pandas as pd
from sklearn.cluster import KMeans
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.manifold import TSNE
import matplotlib.cm as cm

def check_pos(tag):
    if tag.startswith('J'):
        return wn.ADJ
    elif tag.startswith('V'):
        return wn.VERB
    elif tag.startswith('N'):
        return wn.NOUN
    elif tag.startswith('R'):
        return wn.ADV
    else:
        return ''

def preprocessing(line): 
    
    stop_file = open("content/stopwords.txt")

    #stopwords
    stopwords = stop_file.readlines()
    stopwords = [x.strip() for x in stopwords] 

    #file retrieving
    read = line
    
    read = read.lower()
    #tokenization
    tokens = word_tokenize(read)

    #removing stopwords
    filtered = [w for w in tokens if not w in stopwords]

    #stemming
    porter = PorterStemmer()
    stemmed = []
    for x in filtered:
        stemmed.append(porter.stem(x))

    #pos tagging
    tags = pos_tag(stemmed)
    #Lemmatizing
    wordnet_lemmatizer = WordNetLemmatizer()
    lemmatized = []
    for x in tags:
        v = check_pos(x[1])
        if(v != ''):
            lemmatized.append(wordnet_lemmatizer.lemmatize(x[0], pos=v))

    #real words retrival
    realwords = []
    frequencies = {}
    for x in lemmatized:
        syns = wn.synsets(x)
        if syns:
            realwords.append(x)
            for syn in syns:
                if syn in frequencies:
                    frequencies[syn] += 1
                else:
                    frequencies[syn] = 1

    strreal = (" ".join(realwords))

    return strreal

list = os.listdir("input/")

files = []
for f in list:
    s = open("input/" + f)
    files.append(s.read())
print("i've read the files")

vectorizer = TfidfVectorizer(preprocessor=preprocessing)
vectors = vectorizer.fit_transform(files)
feature_names = vectorizer.get_feature_names()

model = KMeans(n_clusters=8).fit(vectors)
clusters = model.predict(vectors)
order_centroids = model.cluster_centers_.argsort()[:, ::-1]

for i in range(8):
    print("Cluster %d:" % i)
    for ind in order_centroids[i, :10]:
        print(' %s' % feature_names[ind]),
    print

pca = PCA(n_components=2).fit_transform(vectors.todense())
tsne = TSNE().fit_transform(PCA(n_components=50).fit_transform(vectors.todense()))

label_subset = [cm.hsv(i/max(clusters)) for i in clusters]

plt.scatter(tsne[:, 0], tsne[:, 1], c=label_subset)

plt.show()