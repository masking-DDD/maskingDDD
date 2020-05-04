import requests
from bs4 import BeautifulSoup
from datetime import datetime
import pandas as pd

import glob
import os.path

now=datetime.now()
nowDate=now.strftime('%Y%m%d')
url = 'https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day&sectionId=100&date='+nowDate

r = requests.get(url)
html = r.content
soup = BeautifulSoup(html, 'html.parser')
#titles_html = soup.select('.ranking ol li a')
titles_html = soup.select('div.ranking_headline a')

for result in titles_html:
    print("제목:",result.string)

pol_news=[]

for titles in titles_html:
    pol_news.append(titles.string)

df=pd.DataFrame(data=pol_news)
#df=pd.DataFrame(data=pol_news, columns=['많이 본 정치 기사'])

df.to_excel("pol_all_news.xlsx")

#files=glob.glob('*.xlsx')
#for x in files:
  #  if not os.path.isdir(x):
    #    x2=x.replace('.xlsx','.xls')
      #  os.rename(x,x2)

#pol_news=[]

#for titles in titles_html:
#    pol_news.append(titles.text)

#for i in range(len(pol_news)):
#    print(pol_news[i])
    
#df=pd.DataFrame(data=pol_news, columns=['메인기사'])
#df.to_excel("pol_news.xlsx")
