import torch
import torch.nn as nn
import torchvision
import torchvision.transforms as transforms

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

sequence_length = 28
input_size = 28
hidden_size = 50
num_layers = 2
num_classes = 10
batch_size = 100
num_epochs = 3
learning_rate = 0.01


class RNN(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers, num_classes):
        super(RNN, self).__init__()
        self.hidden_size = hidden_size
        self.num_layers = num_layers
        self.lstm = nn.LSTM(input_size, hidden_size, num_layers, batch_first=True)
        self.fc = nn.Linear(hidden_size * sequence_length, num_classes)

    def forward(self, x):
        h0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size).to(device)
        c0 = torch.zeros(self.num_layers, x.size(0), self.hidden_size).to(device)

        out, _ = self.lstm(x, (h0, c0))

        out = self.fc(out[:, -1, :])
        return out


train_dataset = torchvision.datasets.MNIST(root='../../data/',
                                           train=True,
                                           transform=transforms.ToTensor(),
                                           download=True)

test_dataset = torchvision.datasets.MNIST(root='../../data/',
                                          train=False,
                                          transform=transforms.ToTensor())


train_loader = torch.utils.data.DataLoader(dataset=train_dataset,
                                           batch_size=batch_size,
                                           shuffle=True)

test_loader = torch.utils.data.DataLoader(dataset=test_dataset,
                                          batch_size=batch_size,
                                          shuffle=False)


model = RNN(input_size, hidden_size, num_layers, num_classes).to(device)

from collections import defaultdict
import matplotlib.pyplot as plt
import time

start_time = time.time();
from matplotlib import rc

history = defaultdict(list)

criterion = nn.CrossEntropyLoss()
optimizer = torch.optim.Adam(model.parameters(), lr=learning_rate)

steps = len(train_loader)

for epoch in range(num_epochs):
    total = 0
    correct = 0
    for i, (images, labels) in enumerate(train_loader):

        images = images.reshape(-1, sequence_length, input_size).to(device)
        labels = labels.to(device)

        outputs = model(images)
        loss = criterion(outputs, labels)

        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

        _, predicted = torch.max(outputs.data, 1)

        if (i + 1) % 10 == 0:

            history['loss'].append(loss.item())
            total += labels.size(0)
            correct += (predicted == labels).sum().item()
            history['acc'].append(correct/total)

print(time.time()-start_time)
model.eval()
with torch.no_grad():
    correct = 0
    total = 0
    for images, labels in test_loader:
        images = images.reshape(-1, sequence_length, input_size).to(device)
        labels = labels.to(device)
        outputs = model(images)
        _, predicted = torch.max(outputs.data, 1)
        total += labels.size(0)
        correct += (predicted == labels).sum().item()

    print('Test Accuracy : {} %'.format(100 * correct / total))


plt.figure(0)
plt.plot(history['loss'], label='train loss')
plt.title('training history')
plt.ylabel('loss')
plt.xlabel('iterations/10')
plt.legend()
plt.annotate('Epoch1', (60, history['loss'][59]))
plt.annotate('Epoch2', (120, history['loss'][119]))
plt.annotate('Epoch3', (180, history['loss'][179]))
plt.show()

plt.figure(1)
plt.plot(history['acc'], label='train accuracy')
plt.title('training history')
plt.ylabel('accuracy')
plt.xlabel('iterations/10')
plt.legend()
plt.ylim([0, 1])
plt.annotate('Epoch1', (60, history['acc'][59]))
plt.annotate('Epoch2', (120, history['acc'][119]))
plt.annotate('Epoch3', (180, history['acc'][179]))
plt.show()

import cv2

"""
path='C:\\Users\\USER\\Desktop\\number.bmp'
img=cv2.imread(path, 0)
img=cv2.resize(img, (28,28))

for i in range(0, 28):
    for j in range(0,28):
        if img[i][j] == 255: img[i][j]=0
        else: img[i][j]=1

my=torch.zeros(100,28,28)
img=torch.from_numpy(img)
my[0]=img
with torch.no_grad():
    i=0
    for images, labels in test_loader:
        i=i+1
        if i==1:
            images = images.reshape(-1, sequence_length, input_size).to(device)
            labels = labels.to(device)
            outputs = model(my)
            _, predicted = torch.max(outputs.data, 1)
            print(my)
"""


