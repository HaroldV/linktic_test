# Usa una imagen base de Node.js
# La versión 18 es una buena opción estable y reciente
FROM node:18-alpine

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /usr/src/app

# Copia los archivos package.json y package-lock.json
# Esto permite que Docker use el caché para los pasos de instalación
# lo que acelera el proceso si las dependencias no cambian
COPY package*.json ./

# Instala las dependencias del proyecto
# El flag --production asegura que solo se instalen las dependencias de producción
RUN npm install --production

# Instala psql client para el script wait-for-it.sh
RUN apk add --no-cache postgresql-client

# Copia el resto del código de la aplicación
COPY . .


# Expone el puerto en el que la aplicación se ejecutará
EXPOSE 3000

# Comando para ejecutar la aplicación cuando el contenedor se inicie
CMD [ "node", "server.js" ]

