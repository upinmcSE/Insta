import React from 'react'
import styles from './hstack.module.css'

const HStack = ({ children, gap = 16, align = 'center', justify = 'flex-start', className = '', style = {}, ...props }) => {
  return (
    <div
      className={`${styles.hstack} ${className}`}
      style={{ gap: `${gap}px`, alignItems: align, justifyContent: justify, ...style }}
      {...props}
    >
      {children}
    </div>
  );
};

export default HStack;
