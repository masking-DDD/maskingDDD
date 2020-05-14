import glob
import os.path

os.remove('pol_all_news.xls')
files=glob.glob('*.xlsx')
for x in files:
    if not os.path.isdir(x):
        x2=x.replace('.xlsx','.xls')
        os.rename(x,x2)
        
