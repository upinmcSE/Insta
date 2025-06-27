import React from 'react'
import styles from './vstack.module.css'

const VStack = ({ children, gap = 16, align = 'stretch', justify = 'flex-start', className = '', style = {}, ...props }) => {
  return (
    <div
      className={`${styles.vstack} ${className}`}
      style={{ gap: `${gap}px`, alignItems: align, justifyContent: justify, ...style }}
      {...props}
    >
      {children}
    </div>
  );
};

export default VStack;
